package admin.core.scheduler;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.util.List;
import java.util.Random;

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
     * 时间窗口，获取距下次触发时间xs内的触发器
     */
    private int timeWindowSize = 0 * 1000;

    /**
     * 调度间隔时间
     */
    private int sleepTime = 20 * 1000;
    private Random sleepRandom = new Random();
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
        log.info("SchedulerThread-{} start", tesseractGroup.getName());
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
                            //这里中断，可能是spring的destroy生命周期调用
                            log.info("调度线程-{}中断,将取消调度:{}", tesseractGroup.getName(), triggerList);
                            continue;
                        }
                    }
                }
                tesseractTriggerDispatcher.dispatchTrigger(triggerList);
                //这里继续调度，由于上面采用分页处理，可能后面还需要调度的触发器
                continue;
            }
            //随机睡眠
            try {
                Thread.sleep(nextScheduleTime());
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
        this.tesseractTriggerDispatcher.stop();
        this.interrupt();
    }

    /**
     * 返回下一次调度时间，最低为2s
     *
     * @return
     */
    private long nextScheduleTime() {
        long sleepTime = sleepRandom.nextInt(this.sleepTime);
        if (sleepTime < 2) {
            sleepTime = 2;
        }
        return sleepTime;
    }
}