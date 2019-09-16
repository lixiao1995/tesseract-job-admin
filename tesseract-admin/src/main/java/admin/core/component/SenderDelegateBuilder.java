package admin.core.component;

import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.TaskExecutorDelegate;
import admin.core.scheduler.service.ITaskService;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
public class SenderDelegateBuilder {

    @Autowired
    private ITaskService feignService;

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
    private TesseractMailSender tesseractMailSender;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    private volatile TaskExecutorDelegate taskExecutorDelegate;

    /**
     * 单例
     *
     * @return
     */
    public TaskExecutorDelegate getTaskExecutorDelegate() {
        if (taskExecutorDelegate == null) {
            synchronized (this) {
                if (taskExecutorDelegate == null) {
                    taskExecutorDelegate = createSendToExecute();
                }
            }
        }
        return taskExecutorDelegate;
    }

    private TaskExecutorDelegate createSendToExecute() {
        TaskExecutorDelegate taskExecutorDelegate = TaskExecutorDelegate.builder()
                .taskService(feignService)
                .firedJobService(firedJobService)
                .groupService(groupService)
                .mailEventBus(mailEventBus)
                .mailTemplate(mailTemplate)
                .tesseractMailSender(tesseractMailSender)
                .tesseractLogService(tesseractLogService)
                .tesseractTriggerService(tesseractTriggerService)
                .build();
        return taskExecutorDelegate;
    }

}
