package admin.core.component;

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
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @projectName: tesseract-job-admin
 * @className: SendToExecuteComponent
 * @description: 创建
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

    public SendToExecute createSendToExecute() {
        SendToExecute sendToExecute = new SendToExecute();
        sendToExecute.setFiredJobService(firedJobService);
        sendToExecute.setMailEventBus(mailEventBus);
        sendToExecute.setMailTemplate(mailTemplate);
        sendToExecute.setTesseractLogService(tesseractLogService);
        sendToExecute.setFeignService(feignService);
        sendToExecute.setGroupService(groupService);
        return sendToExecute;
    }

}
