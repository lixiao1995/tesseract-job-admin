package admin.core.scanner;

import admin.service.ITesseractTriggerService;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.lifecycle.IThreadLifycycle;

/**
 * 〈扫描错过时间未触发的触发器产生报警〉
 *
 * @author nickel
 * @create 2019/7/12
 * @since 1.0.0
 */
@Slf4j
public class MissfireScanner extends Thread implements IThreadLifycycle {


    private ITesseractTriggerService triggerService;
    private volatile boolean isStop = false;
    private Long scanIntervalTime = 30 * 1000L;
    private Integer missfireTriggerBatchSize = 50;
    private Long missfireTime = scanIntervalTime;

    public MissfireScanner(ITesseractTriggerService triggerService) {
        super("MissfireScanner");
        this.triggerService = triggerService;
    }

    @Override
    public void run() {
        log.info("MissfireScanner start");
        while (!isStop) {
            boolean hasMore = triggerService.resovleMissfireTrigger(missfireTriggerBatchSize, System.currentTimeMillis() - missfireTime);
            if (hasMore) {
                continue;
            }
            try {
                Thread.sleep(scanIntervalTime);
            } catch (InterruptedException e) {
            }
        }
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
