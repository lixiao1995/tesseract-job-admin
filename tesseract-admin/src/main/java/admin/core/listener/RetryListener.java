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
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.EventBus;
import feignService.IAdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tesseract.core.dto.TesseractAdminJobNotify;

import javax.validation.constraints.NotNull;
import java.util.List;


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
@Component
public class RetryListener {

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    @Autowired
    private ITesseractFiredJobService tesseractFiredJobService;
    @Autowired
    private ITesseractJobDetailService tesseractJobDetailService;
    @Autowired
    private ITesseractExecutorDetailService tesseractExecutorDetailService;

    @Autowired
    private SendToExecuteComponent sendToExecuteComponent;

    @EventListener
    public void onApplicationEvent(RetryEvent event) {

        TesseractAdminJobNotify jobNotify = (TesseractAdminJobNotify) event.getSource();
        // 重试策略
        @NotNull Integer triggerId = jobNotify.getTriggerId();
        TesseractTrigger tesseractTrigger = tesseractTriggerService.getById(triggerId);
        @NotNull Integer retryCount = tesseractTrigger.getRetryCount();
        @NotNull Long logId = jobNotify.getLogId();
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
            executorDetailList.remove(executorDetail);
            sendToExecute.routerExecute(jobDetail,executorDetailList,tesseractTrigger);

        }else {
            //发邮件
            sendToExecute.doFail("job超过重试次数",tesseractTrigger);
        }
    }




}
