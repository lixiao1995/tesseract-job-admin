package admin.core.listener;

import admin.core.component.SendToExecuteComponent;
import admin.core.event.RetryEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.SendToExecute;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractJobDetailService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import feignService.IAdminFeignService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tesseract.core.dto.TesseractAdminJobNotify;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


/**
 * @projectName: tesseract-job-admin
 * @className: RetryListener
 * @description: 重试监听
 * @author: liangxuekai
 * @createDate: 2019-07-17 21:25
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-17 21:25
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class RetryListener {

    private ITesseractTriggerService tesseractTriggerService;
    private ITesseractFiredJobService tesseractFiredJobService;
    private ITesseractJobDetailService tesseractJobDetailService;
    private ITesseractExecutorDetailService tesseractExecutorDetailService;
    private SendToExecuteComponent sendToExecuteComponent;
    private ITesseractLogService tesseractLogService;

    @Subscribe
    @AllowConcurrentEvents
    public void retry(RetryEvent event) {
        log.info("监听到重试事件请求,参数为:{}", JSON.toJSONString(event.getJobNotify()));
        TesseractAdminJobNotify jobNotify = event.getJobNotify();
        // 重试策略
        @NotNull Integer triggerId = jobNotify.getTriggerId();
        TesseractTrigger tesseractTrigger = tesseractTriggerService.getById(triggerId);
        @NotNull Integer retryCount = tesseractTrigger.getRetryCount();
        @NotNull Long logId = jobNotify.getLogId();
        TesseractLog tesseractLog = tesseractLogService.getById(logId);
        QueryWrapper<TesseractFiredJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TesseractFiredJob::getLogId, logId)
                .eq(TesseractFiredJob::getJobId, triggerId);
        TesseractFiredJob firedJob = tesseractFiredJobService.getOne(queryWrapper);
        @NotNull Integer jobId = jobNotify.getJobId();
        TesseractJobDetail jobDetail = tesseractJobDetailService.getById(jobId);
        SendToExecute sendToExecute = sendToExecuteComponent.createSendToExecute();
        QueryWrapper<TesseractExecutorDetail> executorDetailAueryWrapper = new QueryWrapper<>();
        TesseractExecutorDetail executorDetail = tesseractExecutorDetailService.getById(jobNotify.getExecutorDetailId());
        executorDetailAueryWrapper.lambda().eq(TesseractExecutorDetail::getExecutorId, executorDetail.getExecutorId());
        List<TesseractExecutorDetail> executorDetailList = tesseractExecutorDetailService.list(executorDetailAueryWrapper);
        if (retryCount > firedJob.getRetryCount()) {
            //开始执行
            //更换机器重试任务
            Optional<TesseractExecutorDetail> detailOptional = executorDetailList.stream().filter(tesseractExecutorDetail -> {
                boolean socketSame = tesseractExecutorDetail.getSocket().equals(executorDetail.getSocket());
                boolean executorIdSame = tesseractExecutorDetail.getExecutorId().equals(executorDetail.getExecutorId());
                return socketSame && executorIdSame;
            }).findFirst();
            TesseractExecutorDetail tesseractExecutorDetail = detailOptional.get();
            if (executorDetailList.size() > 1) {
                executorDetailList.remove(tesseractExecutorDetail);
            }
            sendToExecute.routerExecute(jobDetail, executorDetailList, tesseractTrigger, tesseractLog);
        } else {
            //发邮件
            sendToExecute.doFail("job超过重试次数", tesseractTrigger);
        }
    }


}
