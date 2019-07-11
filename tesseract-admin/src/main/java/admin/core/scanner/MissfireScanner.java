package admin.core.scanner;

import admin.core.event.MailEvent;
import admin.core.mail.TesseractMailTemplate;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractGroupService;
import admin.service.ITesseractTriggerService;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.util.CollectionUtils;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 〈扫描错过时间未触发的触发器产生报警〉
 *
 * @author nickel
 * @create 2019/7/12
 * @since 1.0.0
 */
@Slf4j
public class MissfireScanner extends Thread implements IThreadLifycycle {
    private TesseractMailTemplate mailTemplate;
    private ITesseractTriggerService triggerService;
    private ITesseractGroupService groupService;
    private volatile boolean isStop = false;
    private Long scanIntervalTime = 30 * 1000L;
    private Integer missfireTriggerBatchSize = 50;
    private EventBus mailEventBus;

    public MissfireScanner(ITesseractTriggerService triggerService) {
        super("MissfireScanner");
        this.triggerService = triggerService;
    }

    @Override
    public void run() {
        log.info("MissfireScanner start");
        while (!isStop) {
            List<TesseractTrigger> listMissfire = triggerService.listMissfire(missfireTriggerBatchSize);
            if (!CollectionUtils.isEmpty(listMissfire)) {
                log.info("missfire trigger:{}", listMissfire);
                listMissfire.parallelStream().forEach(trigger -> mailEventBus.post(buildMailEvent(trigger)));
            }
            try {
                Thread.sleep(scanIntervalTime);
            } catch (InterruptedException e) {
            }
        }
    }

    private MailEvent buildMailEvent(TesseractTrigger trigger) {
        MailEvent mailEvent = new MailEvent();
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
