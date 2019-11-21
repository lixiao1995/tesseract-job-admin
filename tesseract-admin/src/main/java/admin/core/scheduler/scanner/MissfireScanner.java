package admin.core.scheduler.scanner;

import admin.core.TesseractJobServiceDelegator;
import admin.entity.TesseractGroup;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.lifecycle.IThreadLifycycle;

import static admin.constant.AdminConstant.SCAN_MISFIRE_JOB_INTERVAL_TIME;

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

    private Integer misfireTriggerBatchSize = 50;
    private TesseractGroup tesseractGroup;

    public MissfireScanner(TesseractGroup tesseractGroup) {
        super(String.format("MisfireScanner-%s", tesseractGroup.getName()));
        this.tesseractGroup = tesseractGroup;
    }

    @Override
    public void run() {
        log.info("线程: {} 启动", this.getName());
        while (!isStop) {
            boolean hasMore = TesseractJobServiceDelegator.triggerService.resovleMissfireTrigger(tesseractGroup,
                    misfireTriggerBatchSize, System.currentTimeMillis() - SCAN_MISFIRE_JOB_INTERVAL_TIME);
            if (hasMore) {
                continue;
            }
            try {
                Thread.sleep(SCAN_MISFIRE_JOB_INTERVAL_TIME);
            } catch (InterruptedException e) {
            }
        }
        log.info("线程: {} 停止", this.getName());
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
