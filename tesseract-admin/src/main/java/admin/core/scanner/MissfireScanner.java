package admin.core.scanner;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractTriggerService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.lifecycle.IThreadLifycycle;
import tesseract.exception.TesseractException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈扫描错过时间未触发的触发器产生报警〉
 *
 * @author nickel
 * @create 2019/7/12
 * @since 1.0.0
 */
@Slf4j
public class MissfireScanner extends Thread implements IThreadLifycycle {
    /**
     * 常量
     */
    private static final String MISSFIRE_TEMPLATE_NAME = "missfireTemplate.html";
    private static final String MISSFIRE_SUBJECT = "Tesseract-job  missfire报警邮件";

    private TesseractMailTemplate mailTemplate;
    private ITesseractTriggerService triggerService;
    private ITesseractGroupService groupService;
    private volatile boolean isStop = false;
    private Long scanIntervalTime = 30 * 1000L;
    private Integer missfireTriggerBatchSize = 50;
    private EventBus mailEventBus;
    private Long missfireTime = scanIntervalTime;

    public MissfireScanner(TesseractMailTemplate mailTemplate, ITesseractTriggerService triggerService, ITesseractGroupService groupService, EventBus mailEventBus) {
        super("MissfireScanner");
        this.mailTemplate = mailTemplate;
        this.triggerService = triggerService;
        this.groupService = groupService;
        this.mailEventBus = mailEventBus;
    }

    @Override
    public void run() {
        log.info("MissfireScanner start");
        while (!isStop) {
            List<TesseractTrigger> listMissfire = triggerService.listMissfireWithLock(missfireTriggerBatchSize, System.currentTimeMillis() - missfireTime);
            log.info("missfire trigger:{}", listMissfire);
            if (!CollectionUtils.isEmpty(listMissfire)) {
                //按组分类后发送邮件，避免发送多个邮件
                Map<Integer, List<TesseractTrigger>> map = new HashMap<>();
                listMissfire.forEach(trigger -> {
                    Integer groupId = trigger.getGroupId();
                    List<TesseractTrigger> triggerList = map.get(groupId);
                    if (triggerList == null) {
                        triggerList = Lists.newArrayList();
                        map.put(groupId, triggerList);
                    }
                    triggerList.add(trigger);
                });
                //发送邮件
                map.entrySet().parallelStream().forEach(entry -> {
                    Integer groupId = entry.getKey();
                    List<TesseractTrigger> triggerList = entry.getValue();
                    MailEvent mailEvent = buildMailEvent(triggerList, groupId);
                    if (mailEvent != null) {
                        mailEventBus.post(mailEvent);
                    }
                });
            }
            try {
                Thread.sleep(scanIntervalTime);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 构建邮件事件
     *
     * @param triggerList
     * @return
     */
    private MailEvent buildMailEvent(List<TesseractTrigger> triggerList, Integer groupId) {
        MailEvent mailEvent = new MailEvent();
        try {
            TesseractGroup tesseractGroup = groupService.getById(groupId);
            if (tesseractGroup == null) {
                throw new TesseractException("没有找到组信息，将无法发送邮件。组id:" + groupId);
            }
            HashMap<String, Object> model = Maps.newHashMap();
            model.put("triggerList", triggerList);
            model.put("groupName", tesseractGroup.getName());
            String body = mailTemplate.buildMailBody(MISSFIRE_TEMPLATE_NAME, model);
            mailEvent.setBody(body);
            mailEvent.setSubject(MISSFIRE_SUBJECT);
            mailEvent.setTo(tesseractGroup.getMail());
        } catch (Exception e) {
            log.error("构建邮件事件异常将无法发送邮件:{}", e.getMessage());
            return null;
        }
        return mailEvent;
    }

    @Override
    public void initThread() {

    }

    @Override
    public void startThread() {
        this.start();
    }

    @Override
    public void stopThread() {
        this.isStop = true;
        this.interrupt();

    }
}
