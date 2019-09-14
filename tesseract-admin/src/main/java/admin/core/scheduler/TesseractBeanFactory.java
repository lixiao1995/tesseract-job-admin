package admin.core.scheduler;

import admin.entity.*;
import tesseract.core.dto.TesseractExecutorRequest;

import static admin.constant.AdminConstant.LOG_FAIL;
import static admin.constant.AdminConstant.SCHEDULER_NAME_MAP;

public class TesseractBeanFactory {
    /**
     * 构建默认日志
     *
     * @return
     */
    public static TesseractLog createDefaultLog(Integer shardingIndex, TesseractTrigger trigger, TesseractJobDetail jobDetail) {
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
     * 构建请求
     *
     * @param logId
     * @param className
     * @param executorDetailId
     * @return
     */
    public static TesseractExecutorRequest createRequest(Long logId,
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
     * 构建正在执行的任务bean
     *
     * @param jobDetail
     * @param executorDetail
     * @param logId
     * @return
     */
    public static TesseractFiredJob createFiredJob(TesseractJobDetail jobDetail,
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
}
