package admin.core.listener;

import admin.core.component.SenderDelegateBuilder;
import admin.core.event.RetryEvent;
import admin.core.scheduler.SenderDelegate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractJobDetailService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * @updateDate: 2019-07-24 15:25
 * @updateRemark: 修改重试次数超过限定次数之后的邮件警告
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
    private SenderDelegateBuilder senderDelegateBuilder;
    private ITesseractLogService tesseractLogService;

    @Subscribe
    @AllowConcurrentEvents
    public void retry(RetryEvent event) {
        log.info("监听到重试事件请求,参数为:{}", JSON.toJSONString(event.getJobNotify()));
        TesseractAdminJobNotify jobNotify = event.getJobNotify();
        // 重试策略
        TesseractTrigger tesseractTrigger = event.getTesseractTrigger();
        @NotNull Integer retryCount = tesseractTrigger.getRetryCount();
        TesseractLog tesseractLog = event.getTesseractLog();
        QueryWrapper<TesseractFiredJob> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TesseractFiredJob::getLogId, tesseractLog.getId())
                .eq(TesseractFiredJob::getJobId, tesseractTrigger.getId());
        TesseractFiredJob firedJob = tesseractFiredJobService.getOne(queryWrapper);
        TesseractJobDetail jobDetail = tesseractJobDetailService.getById(firedJob.getJobId());
        SenderDelegate senderDelegate = senderDelegateBuilder.getSenderDelegate();
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
            senderDelegate.routerExecute(jobDetail, executorDetailList, tesseractTrigger, tesseractLog);
        } else {
            //发邮件
            senderDelegate.doFail(tesseractLog);
        }
    }


}
