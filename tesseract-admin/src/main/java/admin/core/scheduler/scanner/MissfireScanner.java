package admin.core.scheduler.scanner;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.entity.TesseractGroup;
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


    private volatile boolean isStop = false;
    private Long scanIntervalTime = 30 * 1000L;
    private Integer missfireTriggerBatchSize = 50;
    private Long missfireTime = scanIntervalTime;
    private TesseractGroup tesseractGroup;

    public MissfireScanner(TesseractGroup tesseractGroup) {
        super(String.format("MissfireScanner-%s", tesseractGroup.getName()));
        this.tesseractGroup = tesseractGroup;
    }

    @Override
    public void run() {
        log.info("MissfireScanner-{} start", tesseractGroup.getName());
        while (!isStop) {
            boolean hasMore = TesseractJobServiceDelegator.triggerService.resovleMissfireTrigger(tesseractGroup, missfireTriggerBatchSize, System.currentTimeMillis() - missfireTime);
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
