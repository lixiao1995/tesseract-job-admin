package admin.core.scheduler;

import admin.constant.AdminConstant;
import admin.core.component.TesseractMailSender;
import admin.core.event.RetryEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.router.impl.HashRouter;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.EventBus;
import feignService.IAdminFeignService;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static admin.constant.AdminConstant.LOG_FAIL;
import static admin.constant.AdminConstant.SCHEDULER_NAME_MAP;
import static admin.constant.AdminConstant.SCHEDULER_STRATEGY_BROADCAST;
import static admin.constant.AdminConstant.SCHEDULER_STRATEGY_SHARDING;
import static admin.constant.AdminConstant.SCHEDULE_ROUTER_MAP;
import static tesseract.core.constant.CommonConstant.EXECUTE_MAPPING;
import static tesseract.core.constant.CommonConstant.HTTP_PREFIX;

/**
 * @projectName: tesseract-job-admin
 * @className: SenderDelegate
 * @description:
 * @author: liangxuekai
 * @createDate: 2019-07-20 11:33
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-20 11:33
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Slf4j
@Data
@Builder
public class SenderDelegate {

    private ITesseractLogService tesseractLogService;

    private ITesseractFiredJobService firedJobService;

    private TesseractMailTemplate mailTemplate;

    private EventBus mailEventBus;

    private IAdminFeignService feignService;

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
        TesseractLog tesseractLog = new TesseractLog();
        if (log == null) {
            tesseractLog = buildDefaultLog(shardingIndex, trigger, jobDetail);
            tesseractLog.setSocket(executorDetail.getSocket());
            tesseractLog.setMsg("执行中");
            //设置结束时间为0 表示未结束
            tesseractLog.setEndTime(0L);
            tesseractLog.setStatus(AdminConstant.LOG_WAIT);
            tesseractLog.setExecutorDetailId(executorDetail.getId());
            tesseractLogService.save(tesseractLog);
            //设置firedTrigger
            firedJobService.save(buildFiredJob(jobDetail, executorDetail, tesseractLog.getId(), trigger));
        } else {
            tesseractLog = log;
        }

        //构建请求发送
        TesseractExecutorRequest tesseractExecutorRequest = buildRequest(tesseractLog.getId(), jobDetail.getId(), jobDetail.getClassName(), executorDetail.getId(), shardingIndex, trigger);
        doRequest(tesseractExecutorRequest, tesseractLog, executorDetail, trigger);
    }

    /**
     * 构建默认日志
     *
     * @return
     */
    private TesseractLog buildDefaultLog(Integer shardingIndex, TesseractTrigger trigger, TesseractJobDetail jobDetail) {
        TesseractLog tesseractLog = new TesseractLog();
        tesseractLog.setClassName(jobDetail.getClassName());
        tesseractLog.setCreateTime(System.currentTimeMillis());
        tesseractLog.setCreator(jobDetail.getCreator());
        tesseractLog.setGroupId(trigger.getGroupId());
        tesseractLog.setGroupName(trigger.getGroupName());
        tesseractLog.setTriggerName(trigger.getName());
        tesseractLog.setEndTime(System.currentTimeMillis());
        tesseractLog.setExecutorDetailId(0);
        tesseractLog.setStatus(LOG_FAIL);
        tesseractLog.setSocket("");
        tesseractLog.setStrategy(SCHEDULER_NAME_MAP.getOrDefault(trigger.getStrategy(), "未知调度<不应该出现>"));
        if (shardingIndex == null) {
            tesseractLog.setShardingIndex(-1);
        } else {
            tesseractLog.setShardingIndex(shardingIndex);
        }
        return tesseractLog;
    }


    /**
     * 构建正在执行的任务bean
     *
     * @param jobDetail
     * @param executorDetail
     * @param logId
     * @return
     */
    private TesseractFiredJob buildFiredJob(TesseractJobDetail jobDetail,
                                            TesseractExecutorDetail executorDetail,
                                            Long logId,
                                            TesseractTrigger trigger) {
        TesseractFiredJob tesseractFiredTrigger = new TesseractFiredJob();
        tesseractFiredTrigger.setCreateTime(System.currentTimeMillis());
        tesseractFiredTrigger.setTriggerName(trigger.getName());
        tesseractFiredTrigger.setTriggerId(trigger.getId());
        tesseractFiredTrigger.setJobId(jobDetail.getId());
        tesseractFiredTrigger.setClassName(jobDetail.getClassName());
        tesseractFiredTrigger.setSocket(executorDetail.getSocket());
        tesseractFiredTrigger.setExecutorDetailId(executorDetail.getId());
        tesseractFiredTrigger.setLogId(logId);
        tesseractFiredTrigger.setRetryCount(0);
        return tesseractFiredTrigger;
    }


    /**
     * 构建请求
     *
     * @param logId
     * @param className
     * @param executorDetailId
     * @return
     */
    private TesseractExecutorRequest buildRequest(Long logId,
                                                  Integer jobId,
                                                  String className,
                                                  Integer executorDetailId,
                                                  Integer shardingIndex,
                                                  TesseractTrigger trigger) {
        TesseractExecutorRequest executorRequest = new TesseractExecutorRequest();
        executorRequest.setJobId(jobId);
        executorRequest.setClassName(className);
        executorRequest.setShardingIndex(trigger.getShardingNum());
        executorRequest.setLogId(logId);
        executorRequest.setTriggerId(trigger.getId());
        executorRequest.setShardingIndex(shardingIndex);
        executorRequest.setExecutorDetailId(executorDetailId);
        return executorRequest;
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
        try {
            response = feignService.sendToExecutor(new URI(HTTP_PREFIX + executorDetail.getSocket() + EXECUTE_MAPPING), executorRequest);
        } catch (URISyntaxException e) {
            log.error("URI异常:{}", e.getMessage());
            response = TesseractExecutorResponse.builder().body("URI异常").status(TesseractExecutorResponse.FAIL_STAUTS).build();
        }
        //执行成功直接返回等待执行后更新日志状态
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
     * 3、更改firedJob
     *
     * @param msg
     */
    public void doFail(String msg, TesseractTrigger trigger, TesseractJobDetail jobDetail) {
        TesseractLog tesseractLog = buildDefaultLog(null, trigger, jobDetail);
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
