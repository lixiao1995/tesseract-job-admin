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
    private TesseractGroup tesseractGroup;

    /**
     * 时间窗口，获取距下次触发时间5s内的触发器
     */
    private int timeWindowSize = 5 * 1000;

    /**
     * 调度间隔时间
     */
    private int sleepTime = 20 * 1000;

    /**
     * 触发容错时间
     */
    private int accurateTime = 1 * 1000;

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
                // 距离最近触发时间1s以上的，等待到触发时间再继续执行
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
            // 下一次发起调度是20s之后
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