package admin.core.scheduler;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.util.List;

/**
 * @author nickle
 * @description 任务调度器
 */
@Slf4j
public class SchedulerThread extends Thread implements IThreadLifycycle {
    private volatile boolean isStop = false;
    private TesseractTriggerDispatcher tesseractTriggerDispatcher;
    private int timeWindowSize = 5 * 1000;
    private int sleepTime = 20 * 1000;
    private int accurateTime = 1 * 1000;
    private TesseractGroup tesseractGroup;

    public SchedulerThread(TesseractGroup tesseractGroup, TesseractTriggerDispatcher tesseractTriggerDispatcher) {
        this.tesseractTriggerDispatcher = tesseractTriggerDispatcher;
        this.tesseractGroup = tesseractGroup;
        this.setName(String.format("SchedulerThread-%s", tesseractGroup.getName()));
    }

    public TesseractTriggerDispatcher getTesseractTriggerDispatcher() {
        return tesseractTriggerDispatcher;
    }

    @Override
    public void run() {
        log.info("SchedulerThread {} start", tesseractGroup.getName());
        while (!isStop) {
            int blockGetAvailableThreadNum = tesseractTriggerDispatcher.blockGetAvailableThreadNum();
            log.info("可用线程数:{}", blockGetAvailableThreadNum);
            List<TesseractTrigger> triggerList = TesseractJobServiceDelegator.triggerService.findTriggerWithLock(tesseractGroup, blockGetAvailableThreadNum, System.currentTimeMillis(), timeWindowSize);
            log.info("扫描触发器数量:{}", triggerList.size());
            if (!CollectionUtils.isEmpty(triggerList)) {
                //降序排序等待时间差
                TesseractTrigger tesseractTrigger = triggerList.get(0);
                Long nextTriggerTime = tesseractTrigger.getNextTriggerTime();
                long time = nextTriggerTime - System.currentTimeMillis();
                if (time > accurateTime) {
                    synchronized (this) {
                        try {
                            this.wait(time);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                tesseractTriggerDispatcher.dispatchTrigger(triggerList);
                continue;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
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