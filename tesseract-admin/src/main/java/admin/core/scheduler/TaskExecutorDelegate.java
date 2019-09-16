package admin.core.scheduler;

import admin.constant.AdminConstant;
import admin.core.component.TesseractMailSender;
import admin.core.event.RetryEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.router.impl.HashRouter;
import admin.core.scheduler.service.ITaskService;
import admin.entity.*;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.EventBus;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.exception.TesseractException;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

import static admin.constant.AdminConstant.*;
import static tesseract.core.constant.CommonConstant.EXECUTE_MAPPING;
import static tesseract.core.constant.CommonConstant.HTTP_PREFIX;

/**
 * @author: nickle, liangxuekai
 * @description: 任务执行器代理，真正执行任务
 * @date: 2019-07-24 16:01
 */
@Slf4j
@Data
@Builder
public class TaskExecutorDelegate {

    private ITesseractLogService tesseractLogService;

    private ITesseractFiredJobService firedJobService;

    private TesseractMailTemplate mailTemplate;

    private EventBus mailEventBus;

    private ITaskService taskService;

    private ITesseractGroupService groupService;

    private TesseractMailSender tesseractMailSender;

    private EventBus retryEventBus;

    private ITesseractTriggerService tesseractTriggerService;


    public void routerExecute(TesseractJobDetail jobDetail,
                              List<TesseractExecutorDetail> executorDetailList,
                              TesseractTrigger trigger) {
        routerExecute(jobDetail, executorDetailList, trigger, null);
    }

    /**
     * 路由执行器
     *
     * @param jobDetail          触发器对应任务
     * @param executorDetailList 机器列表
     */
    public void routerExecute(TesseractJobDetail jobDetail,
                              List<TesseractExecutorDetail> executorDetailList,
                              TesseractTrigger trigger,
                              TesseractLog log) {
        //路由选择
        @NotNull Integer strategy = trigger.getStrategy();
        //广播
        if (SCHEDULER_STRATEGY_BROADCAST.equals(strategy)) {
            /**
             * 并行发送任务到所有机器执行
             */
            executorDetailList.parallelStream().forEach(executorDetail -> buildRequestAndSend(jobDetail, executorDetail, null, trigger, log));
        } else if (SCHEDULER_STRATEGY_SHARDING.equals(strategy)) {
            //分片
            @NotNull Integer shardingNum = trigger.getShardingNum();
            int size = executorDetailList.size();
            int count = 0;
            for (int i = 0; i < shardingNum; i++) {
                if (i < size) {
                    count = 0;
                }
                buildRequestAndSend(jobDetail, executorDetailList.get(count), count++, trigger, log);
            }
        } else {
            //正常调用
            TesseractExecutorDetail executorDetail = SCHEDULE_ROUTER_MAP.getOrDefault(trigger.getStrategy(), new HashRouter()).routerExecutor(executorDetailList);
            buildRequestAndSend(jobDetail, executorDetail, null, trigger, log);
        }
    }

    /**
     * 构建请求并发送
     *
     * @param jobDetail
     * @param executorDetail
     */
    private void buildRequestAndSend(TesseractJobDetail jobDetail,
                                     TesseractExecutorDetail executorDetail,
                                     Integer shardingIndex,
                                     TesseractTrigger trigger,
                                     TesseractLog log) {
        TesseractLog tesseractLog;
        if (log == null) {
            tesseractLog = TesseractBeanFactory.createDefaultLog(shardingIndex, trigger, jobDetail);
            tesseractLog.setSocket(executorDetail.getSocket());
            tesseractLog.setMsg("执行中");
            //设置结束时间为0 表示未结束
            tesseractLog.setEndTime(0L);
            tesseractLog.setStatus(AdminConstant.LOG_WAIT);
            tesseractLog.setExecutorDetailId(executorDetail.getId());
            tesseractLogService.save(tesseractLog);
            //设置firedTrigger
            TesseractFiredJob firedJob = TesseractBeanFactory.createFiredJob(jobDetail, executorDetail, tesseractLog.getId(), trigger);
            firedJobService.save(firedJob);
        } else {
            tesseractLog = log;
        }

        //构建请求发送
        TesseractExecutorRequest tesseractExecutorRequest = TesseractBeanFactory.createRequest(tesseractLog.getId(),
                jobDetail.getId(), jobDetail.getClassName(), executorDetail.getId(), shardingIndex, trigger);
        doRequest(tesseractExecutorRequest, tesseractLog, executorDetail, trigger);
    }


    /**
     * 发送调度请求
     *
     * @param executorRequest
     * @param tesseractLog
     * @param executorDetail
     */
    private void doRequest(TesseractExecutorRequest executorRequest,
                           TesseractLog tesseractLog,
                           TesseractExecutorDetail executorDetail,
                           TesseractTrigger trigger) {
        log.info("开始调度:{}", executorRequest);
        TesseractExecutorResponse response;
        String socket = executorDetail.getSocket();
        try {
            response = taskService.sendToExecutor(new URI(HTTP_PREFIX + socket + EXECUTE_MAPPING), executorRequest);
        } catch (TesseractException e) {
            log.error("发起调度异常", e);
            taskService.errorHandle(socket);
            response = TesseractExecutorResponse.builder().body(e.getMsg()).status(TesseractExecutorResponse.FAIL_STAUTS).build();
        } catch (Exception e) {
            log.error("发起调度异常", e);
            response = TesseractExecutorResponse.builder().body(e.getMessage()).status(TesseractExecutorResponse.FAIL_STAUTS).build();
        }
        //发送任务成功直接返回等待执行后更新日志状态
        if (response.getStatus() == TesseractExecutorResponse.SUCCESS_STATUS) {
            return;
        }
        //如果执行失败则更新日志状态并且移出执行表
        tesseractLog.setStatus(AdminConstant.LOG_FAIL);
        tesseractLog.setEndTime(System.currentTimeMillis());
        Object body = response.getBody();
        if (body != null) {
            tesseractLog.setMsg(body.toString());
        }

        //判断是否超过重试次数，如果超过重试次数则移出执行表，如果没超过，则+1
        QueryWrapper<TesseractFiredJob> firedJobQueryWrapper = new QueryWrapper<>();
        firedJobQueryWrapper.lambda().eq(TesseractFiredJob::getLogId, tesseractLog.getId());
        TesseractFiredJob tesseractFiredJob = firedJobService.getOne(firedJobQueryWrapper);
        if (trigger.getRetryCount() > tesseractFiredJob.getRetryCount()) {
            tesseractFiredJob.setRetryCount(tesseractFiredJob.getRetryCount() + 1);
            firedJobService.updateById(tesseractFiredJob);
            //发布重试事件
            TesseractAdminJobNotify tesseractAdminJobNotify = new TesseractAdminJobNotify();
            tesseractAdminJobNotify.setExecutorDetailId(executorDetail.getId());
            tesseractAdminJobNotify.setLogId(tesseractLog.getId());
            RetryEvent retryEvent = new RetryEvent(tesseractAdminJobNotify, trigger, tesseractLog);
            retryEventBus.post(retryEvent);
        }
        //失败执行
        doFail(tesseractLog);
    }


    /**
     * 保存失败日志并产生报警邮件
     * 1、更改日志状态
     * 2、发送邮件
     *
     * @param msg
     */
    public void doFail(String msg, TesseractTrigger trigger, TesseractJobDetail jobDetail) {
        TesseractLog tesseractLog = TesseractBeanFactory.createDefaultLog(trigger.getShardingNum(), trigger, jobDetail);
        tesseractLog.setMsg(msg);
        tesseractLogService.save(tesseractLog);
        tesseractMailSender.logSendMail(tesseractLog);
    }

    /**
     * 更新失败日志并产生报警邮件
     * 1、更改日志状态
     * 2、发送邮件
     * 3、更改firedJob
     *
     * @param tesseractLog
     */
    public void doFail(TesseractLog tesseractLog) {
        tesseractLog.setStatus(LOG_FAIL);
        tesseractLogService.updateById(tesseractLog);
        log.info("tesseractLog:{}", tesseractLog);
        tesseractMailSender.logSendMail(tesseractLog);

        QueryWrapper<TesseractFiredJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TesseractFiredJob::getLogId, tesseractLog.getId());
        TesseractFiredJob tesseractFiredJob = firedJobService.getOne(queryWrapper);
        TesseractTrigger tesseractTrigger = tesseractTriggerService.getById(tesseractFiredJob.getTriggerId());
        if (tesseractTrigger.getRetryCount() <= tesseractFiredJob.getRetryCount()) {
            firedJobService.remove(queryWrapper);
        }
    }


}
