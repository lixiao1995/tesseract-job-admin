package admin.core.scheduler;

import admin.constant.AdminConstant;
import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.core.scheduler.bean.CurrentTaskInfo;
import admin.core.scheduler.bean.TaskContextInfo;
import admin.core.scheduler.router.impl.HashRouter;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static admin.constant.AdminConstant.*;
import static admin.core.netty.server.TesseractJobServiceDelegator.*;
import static tesseract.core.constant.CommonConstant.EXECUTE_MAPPING;
import static tesseract.core.constant.CommonConstant.HTTP_PREFIX;

/**
 * @author: nickle, liangxuekai
 * @description: 任务执行器代理，真正执行任务
 * @date: 2019-07-24 16:01
 */
@Slf4j
@Data
public class TaskExecutorDelegate {

    /**
     * 路由执行器
     *
     * @param taskContextInfo 任务上下文
     */
    public static void routerExecute(TaskContextInfo taskContextInfo) {
        TesseractTrigger trigger = taskContextInfo.getTrigger();
        List<TesseractExecutorDetail> executorDetailList = taskContextInfo.getExecutorDetailList();
        //路由选择
        @NotNull Integer strategy = trigger.getStrategy();
        if (SCHEDULER_STRATEGY_BROADCAST.equals(strategy)) {
            executeBroadcast(taskContextInfo);
        } else if (SCHEDULER_STRATEGY_SHARDING.equals(strategy)) {
            executeSharding(taskContextInfo);
        } else {
            //正常调用
            executeGeneral(taskContextInfo);
        }
    }

    /**
     * 正常调用，除去：广播和分片
     *
     * @param taskContextInfo
     */
    private static void executeGeneral(TaskContextInfo taskContextInfo) {
        TesseractTrigger trigger = taskContextInfo.getTrigger();
        List<TesseractExecutorDetail> executorDetailList = taskContextInfo.getExecutorDetailList();
        CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
        TesseractExecutorDetail executorDetail = SCHEDULE_ROUTER_MAP.getOrDefault(trigger.getStrategy()
                , new HashRouter()).routerExecutor(executorDetailList);
        currentTaskInfo.setCurrentExecutorDetail(executorDetail);
        buildRequestAndSend(currentTaskInfo);
    }

    /**
     * 分片逻辑
     *
     * @param taskContextInfo
     */
    private static void executeSharding(TaskContextInfo taskContextInfo) {
        TesseractTrigger trigger = taskContextInfo.getTrigger();
        List<TesseractExecutorDetail> executorDetailList = taskContextInfo.getExecutorDetailList();
        @NotNull Integer shardingNum = trigger.getShardingNum();
        int size = executorDetailList.size();
        int count = 0;
        for (int i = 0; i < shardingNum; i++) {
            if (i < size) {
                count = 0;
            }
            CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
            currentTaskInfo.setShardingIndex(count);
            currentTaskInfo.setCurrentExecutorDetail(executorDetailList.get(count));
            count++;
            buildRequestAndSend(currentTaskInfo);
        }
    }

    /**
     * 执行广播，并行发送任务到所有机器执行
     *
     * @param taskContextInfo
     */
    private static void executeBroadcast(TaskContextInfo taskContextInfo) {
        taskContextInfo.getExecutorDetailList().parallelStream().forEach(executorDetail -> {
            CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
            currentTaskInfo.setCurrentExecutorDetail(executorDetail);
            buildRequestAndSend(currentTaskInfo);
        });
    }

    /**
     * 构建请求并发送
     *
     * @param currentTaskInfo
     */
    private static void buildRequestAndSend(CurrentTaskInfo currentTaskInfo) {
        TesseractExecutorDetail currentExecutorDetail = currentTaskInfo.getCurrentExecutorDetail();
        TesseractLog tesseractLog = TesseractBeanFactory.createDefaultLog(currentTaskInfo);
        tesseractLog.setSocket(currentExecutorDetail.getSocket());
        tesseractLog.setMsg("执行中");
        //设置结束时间为0 表示未结束
        tesseractLog.setEndTime(0L);
        tesseractLog.setStatus(AdminConstant.LOG_WAIT);
        tesseractLog.setExecutorDetailId(currentExecutorDetail.getId());
        currentTaskInfo.setLog(tesseractLog);
        logService.save(tesseractLog);
        //设置firedTrigger
        TesseractFiredJob firedJob = TesseractBeanFactory.createFiredJob(currentTaskInfo);
        firedJobService.save(firedJob);

        //构建请求发送
        TesseractExecutorRequest tesseractExecutorRequest = TesseractBeanFactory.createRequest(currentTaskInfo);
        currentTaskInfo.setExecutorRequest(tesseractExecutorRequest);
        doRequest(currentTaskInfo);
    }


    /**
     * 发送调度请求
     *
     * @param currentTaskInfo
     */
    private static void doRequest(CurrentTaskInfo currentTaskInfo) {
        TesseractExecutorRequest executorRequest = currentTaskInfo.getExecutorRequest();
        TesseractExecutorDetail currentExecutorDetail = currentTaskInfo.getCurrentExecutorDetail();
        TesseractLog tesseractLog = currentTaskInfo.getLog();
        TesseractExecutorResponse response;
        String socket = currentExecutorDetail.getSocket();
        try {
            TaskExecutorDelegate.log.info("开始调度:{}", executorRequest);
            response = TesseractJobServiceDelegator.taskService.sendToExecutor(new URI(HTTP_PREFIX + socket + EXECUTE_MAPPING), executorRequest);
        } catch (TesseractException e) {
            TaskExecutorDelegate.log.error("发起调度异常", e);
            TesseractJobServiceDelegator.taskService.errorHandle(socket);
            response = TesseractExecutorResponse.builder().body(e.getMsg()).status(TesseractExecutorResponse.FAIL_STAUTS).build();
        } catch (Exception e) {
            TaskExecutorDelegate.log.error("发起调度异常", e);
            response = TesseractExecutorResponse.builder().body(e.getMessage()).status(TesseractExecutorResponse.FAIL_STAUTS).build();
        }
        //发送任务成功直接返回等待执行后更新日志状态
        if (response.getStatus() == TesseractExecutorResponse.SUCCESS_STATUS) {
            return;
        }
        //执行失败逻辑
        tesseractLog.setStatus(LOG_FAIL);
        tesseractLog.setEndTime(System.currentTimeMillis());
        Object body = response.getBody();
        if (body != null) {
            tesseractLog.setMsg(body.toString());
        }
        retry(currentTaskInfo);
    }

    /**
     * 失败重试：
     * 1、移除当前失败执行器，小于重试次数，更新日志为失败且发送邮件
     * 2、大于重试次数，更新日志为失败且发送邮件，移除fire job
     *
     * @param currentTaskInfo
     */
    private static void retry(CurrentTaskInfo currentTaskInfo) {
        List<TesseractExecutorDetail> executorDetailList = currentTaskInfo.getTaskContextInfo().getExecutorDetailList();
        TesseractLog tesseractLog = currentTaskInfo.getLog();
        TesseractTrigger trigger = currentTaskInfo.getTaskContextInfo().getTrigger();
        TesseractFiredJob firedJob = currentTaskInfo.getFiredJob();
        //如果执行机器大于1且小于重试次数则开始重试
        tesseractLog.setRetryCount(firedJob.getRetryCount());
        if (executorDetailList.size() > 1 && firedJob.getRetryCount() < trigger.getRetryCount()) {
            removeExecutorDetail(executorDetailList, currentTaskInfo.getCurrentExecutorDetail());
            doFailWithoutFireJob(currentTaskInfo);
            firedJob.setRetryCount(firedJob.getRetryCount() + 1);
            firedJobService.updateById(firedJob);
            //重试
            buildRequestAndSend(currentTaskInfo);
        } else {
            doFailWithFireJob(currentTaskInfo);
        }
    }

    /**
     * 移除指定的执行器
     */
    private static void removeExecutorDetail(List<TesseractExecutorDetail> executorDetailList, TesseractExecutorDetail needRemoveDetail) {
        Iterator<TesseractExecutorDetail> iterator = executorDetailList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == needRemoveDetail) {
                iterator.remove();
            }
        }
    }

    /**
     * 保存失败日志并产生报警邮件
     * 1、更改日志状态
     * 2、发送邮件
     *
     * @param msg             报错信息
     * @param taskContextInfo 触发器任务上下文
     */
    public static void doFail(String msg, TaskContextInfo taskContextInfo) {
        CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
        TesseractLog tesseractLog = TesseractBeanFactory.createDefaultLog(currentTaskInfo);
        tesseractLog.setMsg(msg);
        tesseractLog.setStatus(LOG_FAIL);
        logService.save(tesseractLog);
        mailSender.logSendMail(tesseractLog);
    }

    /**
     * 保存失败日志并产生报警邮件
     * 1、保存日志状态
     * 2、发送邮件
     *
     * @param currentTaskInfo 当前执行的任务上下文
     */
    private static void doFailWithoutFireJob(CurrentTaskInfo currentTaskInfo) {
        log.info("执行任务失败:{},开始记录日志并发送邮件", currentTaskInfo);
        TesseractLog tesseractLog = currentTaskInfo.getLog();
        logService.updateById(tesseractLog);
        mailSender.logSendMail(tesseractLog);
    }

    /**
     * 更新失败日志并产生报警邮件
     * 1、更改日志状态
     * 2、发送邮件
     * 3、更改firedJob
     *
     * @param currentTaskInfo
     */
    public static void doFailWithFireJob(CurrentTaskInfo currentTaskInfo) {
        TesseractFiredJob firedJob = currentTaskInfo.getFiredJob();
        firedJobService.removeById(firedJob);
        doFailWithoutFireJob(currentTaskInfo);
    }
}
