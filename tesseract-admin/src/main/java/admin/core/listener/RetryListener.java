package admin.core.listener;

import admin.core.event.RetryEvent;
import admin.core.scheduler.TaskExecutorDelegate;
import admin.core.scheduler.bean.CurrentTaskInfo;
import admin.core.scheduler.bean.TaskContextInfo;
import admin.entity.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static admin.core.TesseractJobServiceDelegator.*;
import static admin.core.scheduler.TaskExecutorDelegate.doFailWithFireJob;

/**
 * 〈监听重试事件〉
 *
 * @author nickel
 * @create 2019/9/26
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@Slf4j
public class RetryListener {

    @Subscribe
    @AllowConcurrentEvents
    public void retry(RetryEvent retryEvent) {
        Integer fireJobId = retryEvent.getFireJobId();
        TesseractFiredJob firedJob = firedJobService.getById(fireJobId);
        TesseractTrigger trigger = triggerService.getById(firedJob.getTriggerId());
        TesseractJobDetail jobDetail = jobDetailService.getById(firedJob.getJobId());
        TesseractExecutorDetail executorDetail = executorDetailService.getById(firedJob.getExecutorDetailId());
        TesseractLog tesseractLog = logService.getById(firedJob.getLogId());
        TaskContextInfo taskContextInfo = buildTaskContextInfo(jobDetail, trigger);
        CurrentTaskInfo currentTaskInfo = buildCurrentTaskInfo(executorDetail, taskContextInfo, firedJob, tesseractLog);
        Integer retryCount = trigger.getRetryCount();
        try {
            if (firedJob.getRetryCount() < retryCount) {
                //重试
                TaskExecutorDelegate.retry(currentTaskInfo, true);
                return;
            }
            log.info("达到重试次数");
            doFailWithFireJob(currentTaskInfo);
        } catch (Exception e) {
            log.error("重试失败");
            doFailWithFireJob(currentTaskInfo);
        }
    }


    public CurrentTaskInfo buildCurrentTaskInfo(TesseractExecutorDetail executorDetail
            , TaskContextInfo taskContextInfo, TesseractFiredJob firedJob, TesseractLog tesseractLog) {
        CurrentTaskInfo currentTaskInfo = new CurrentTaskInfo(taskContextInfo);
        currentTaskInfo.setCurrentExecutorDetail(executorDetail);
        currentTaskInfo.setFiredJob(firedJob);
        currentTaskInfo.setLog(tesseractLog);
        currentTaskInfo.setRetry(true);
        currentTaskInfo.setShardingIndex(firedJob.getShardingIndex());
        return currentTaskInfo;
    }

    /**
     * 构建task context info
     *
     * @param jobDetail
     * @param trigger
     * @return
     */
    private TaskContextInfo buildTaskContextInfo(TesseractJobDetail jobDetail, TesseractTrigger trigger) {
        TaskContextInfo taskContextInfo = new TaskContextInfo();
        QueryWrapper<TesseractExecutorDetail> executorDetailQueryWrapper = new QueryWrapper<>();
        executorDetailQueryWrapper.lambda().eq(TesseractExecutorDetail::getExecutorId, trigger.getExecutorId());
        List<TesseractExecutorDetail> list = executorDetailService.list(executorDetailQueryWrapper);
        taskContextInfo.setExecutorDetailList(list);
        taskContextInfo.setJobDetail(jobDetail);
        taskContextInfo.setTrigger(trigger);
        return taskContextInfo;
    }
}
