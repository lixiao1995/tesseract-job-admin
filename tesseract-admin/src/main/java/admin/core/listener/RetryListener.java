package admin.core.listener;

import admin.core.event.RetryEvent;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import feignService.IAdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import tesseract.core.dto.TesseractAdminJobNotify;


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
    private IAdminFeignService feignService;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    @Autowired
    private ITesseractLogService tesseractLogService;


    @EventListener
    public void onApplicationEvent(RetryEvent event) {

        TesseractAdminJobNotify jobNotify = (TesseractAdminJobNotify) event.getSource();
        // 重试策略
        // 是否会记录同一个任务执行过几次
        Integer triggerId = jobNotify.getTriggerId();
        Assert.isNull(triggerId, "triggerId can not be null");
        TesseractTrigger tesseractTrigger = tesseractTriggerService.getById(triggerId);
        Integer retryCount = tesseractTrigger.getRetryCount();
        Assert.isNull(retryCount, "retryCount can not be null");

//        if (retryCount > count) {
            //开始执行
//            feignService.sendToExecutor()
//        }


    }
}
