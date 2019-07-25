package admin.core.component;

import admin.core.listener.RetryListener;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.SendToExecute;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.google.common.eventbus.EventBus;
import feignService.IAdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @projectName: tesseract-job-admin
 * @className: SendToExecuteComponent
 * @description: 创建任务执行DTO
 * @author: liangxuekai
 * @createDate: 2019-07-20 14:24
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-20 14:24
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@Component
public class SendToExecuteComponent {

    @Autowired
    private IAdminFeignService feignService;

    @Autowired
    private ITesseractLogService tesseractLogService;

    @Autowired
    private ITesseractFiredJobService firedJobService;

    @Autowired
    @Qualifier("mailEventBus")
    private EventBus mailEventBus;

    @Autowired
    private TesseractMailTemplate mailTemplate;

    @Autowired
    private ITesseractGroupService groupService;

    @Autowired
    private SendMailComponent sendMailComponent;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    private SendToExecute sendToExecute = null;


    /**
     * 单例
     * @return
     */
    public SendToExecute getSendToExecute(){
        synchronized (new Byte[1]){
            if(sendToExecute == null){
                sendToExecute = createSendToExecute();
            }
        }
        return sendToExecute;
    }


    private SendToExecute createSendToExecute() {
        SendToExecute sendToExecute = SendToExecute.builder()
                .feignService(feignService)
                .firedJobService(firedJobService)
                .groupService(groupService)
                .mailEventBus(mailEventBus)
                .mailTemplate(mailTemplate)
                .sendMailComponent(sendMailComponent)
                .tesseractLogService(tesseractLogService)
                .tesseractTriggerService(tesseractTriggerService)
                .build();
        return sendToExecute;
    }

}
