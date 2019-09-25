package admin.core.component;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractFiredJob;
import admin.entity.TesseractGroup;
import admin.entity.TesseractLog;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractLogService;
import admin.service.ITesseractTriggerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tesseract.core.dto.TesseractAdminJobNotify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static admin.util.AdminUtils.epochMiliToString;

/**
 * @projectName: tesseract-job-admin
 * @className: SendMailComponent
 * @description: 邮件发送组件，所有的发送邮件的动作都在这里面执行
 * @author: liangxuekai
 * @createDate: 2019-07-23 14:51
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-24 15:51
 * @updateRemark: 添加任务重试失败邮件组件
 * @version: 1.0
 */
@Component
public class TesseractMailSender {

    @Autowired
    private EventBus mailEventBus;
    @Autowired
    private ITesseractGroupService groupService;
    @Autowired
    private TesseractMailTemplate mailTemplate;
    @Autowired
    private ITesseractLogService tesseractLogService;
    @Autowired
    private ITesseractTriggerService tesseractTriggerService;
    @Autowired
    private ITesseractFiredJobService tesseractFiredJobService;

    private static final String EXECUTOR_TEMPLATE_NAME = "executorTemplate.html";
    private static final String EXECUTOR_SUBJECT = "Tesseract-job 执行器报警邮件";

    private static final String MISSIONFAILED_TEMPLATE_NAME = "missionFailedTemplate.html";
    private static final String MISSIONFAILED_SUBJECT = "任务执行失败邮件报警";

    private static final String LOG_TEMPLATE_NAME = "logTemplate.html";
    private static final String LOG_SUBJECT = "Tesseract-job日志报警邮件";


    /**
     * executorDetai发送报警邮件
     *
     * @param executorDetailMap key：group id value：组下的detail
     */
    public void executorDetailListExceptionSendMail(Map<Integer, List<TesseractExecutorDetail>> executorDetailMap) {
        Set<Map.Entry<Integer, List<TesseractExecutorDetail>>> entries = executorDetailMap.entrySet();
        entries.parallelStream().forEach(entry -> {
            Integer groupId = entry.getKey();
            List<TesseractExecutorDetail> executorDetailList = entry.getValue();
            HashMap<String, Object> model = Maps.newHashMap();
            model.put("executorDetailList", executorDetailList);
            MailEvent mailEvent = new MailEvent();
            mailEvent.setBody(mailTemplate.buildMailBody(EXECUTOR_TEMPLATE_NAME, model));
            mailEvent.setSubject(EXECUTOR_SUBJECT);
            mailEvent.setTo(groupService.getById(groupId).getMail());
            mailEventBus.post(mailEvent);
        });
    }


    /**
     * 失败后发送报警邮件
     *
     * @param tesseractLog
     */
    public void logSendMail(TesseractLog tesseractLog) {
        TesseractGroup group = groupService.getById(tesseractLog.getGroupId());
        QueryWrapper<TesseractFiredJob> detailQueryWrapper = new QueryWrapper<>();
        detailQueryWrapper.lambda().eq(TesseractFiredJob::getLogId, tesseractLog.getId());
        TesseractFiredJob tesseractFiredJob = tesseractFiredJobService.getOne(detailQueryWrapper);
        TesseractTrigger tesseractTrigger = null;
        if (tesseractFiredJob != null) {
            tesseractTrigger = tesseractTriggerService.getById(tesseractFiredJob.getTriggerId());
        }
        HashMap<String, Object> model = Maps.newHashMap();
        model.put("log", tesseractLog);
        model.put("tesseractFiredJob", tesseractFiredJob);
        model.put("tesseractTrigger", tesseractTrigger);
        model.put("createTime", epochMiliToString(tesseractLog.getCreateTime(), null));
        model.put("endTime", epochMiliToString(tesseractLog.getEndTime(), null));
        String body = mailTemplate.buildMailBody(LOG_TEMPLATE_NAME, model);
        MailEvent mailEvent = new MailEvent();
        mailEvent.setBody(body);
        mailEvent.setSubject(LOG_SUBJECT);
        mailEvent.setTo(group.getMail());
        mailEventBus.post(mailEvent);
    }

    /**
     * 失败后发送报警邮件
     *
     * @param tesseractLogId
     */
    public void logSendMail(Long tesseractLogId) {
        TesseractLog tesseractLog = tesseractLogService.getById(tesseractLogId);
        logSendMail(tesseractLog);
    }

    /**
     * 任务失败发送报警邮件
     *
     * @param tesseractAdminJobNotify
     */
    public void missionFailedSendMail(TesseractAdminJobNotify tesseractAdminJobNotify) {
        logSendMail(tesseractAdminJobNotify.getLogId());
    }


}
