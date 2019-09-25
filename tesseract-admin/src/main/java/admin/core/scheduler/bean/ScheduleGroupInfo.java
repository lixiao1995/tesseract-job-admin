package admin.core.scheduler.bean;

import admin.core.scheduler.SchedulerThread;
import admin.core.scheduler.scanner.ExecutorScanner;
import admin.core.scheduler.scanner.MissfireScanner;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 任务组信息
 * @author: nickle
 * @create: 2019-09-25 15:07
 **/
@Data
@AllArgsConstructor
public class ScheduleGroupInfo {
    /**
     * 执行机器扫描线程
     */
    private ExecutorScanner executorScanner;
    /**
     * 到时未触发任务扫描线程
     */
    private MissfireScanner missfireScanner;
    /**
     * 任务调度线程
     */
    private SchedulerThread schedulerThread;

    /**
     * 开启所有线程
     */
    public void startThreadGroup() {
        if (executorScanner != null) {
            executorScanner.startThread();
        }

        if (missfireScanner != null) {
            missfireScanner.startThread();
        }

        if (schedulerThread != null) {
            schedulerThread.startThread();
        }
    }

    /**
     * 关闭所有线程
     */
    public void stopThreadGroup() {
        if (executorScanner != null) {
            executorScanner.stopThread();
        }

        if (missfireScanner != null) {
            missfireScanner.stopThread();
        }

        if (schedulerThread != null) {
            schedulerThread.stopThread();
        }
    }
}
