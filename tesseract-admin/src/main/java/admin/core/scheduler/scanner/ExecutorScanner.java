package admin.core.scheduler.scanner;

import admin.core.TesseractJobServiceDelegator;
import admin.entity.TesseractGroup;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.lifecycle.IThreadLifycycle;

import static admin.constant.AdminConstant.SCAN_INVALID_EXECUTOR_DETAIL_INTERVAL_TIME;

/**
 * 失效执行器扫描线程
 *
 * @author nickle
 */
@Slf4j
public class ExecutorScanner extends Thread implements IThreadLifycycle {
    private TesseractGroup tesseractGroup;
    private volatile boolean isStop = false;

    public ExecutorScanner(TesseractGroup tesseractGroup) {
        super(String.format("ExecutorScanner-%s", tesseractGroup.getName()));
        this.tesseractGroup = tesseractGroup;
    }

    /**
     * 扫描失去心跳的机器，如果失去心跳：
     * 1、删除executor_detail中数据
     * 2、修改正在等待执行的日志为失败
     * 3、发送报警邮件
     * 4、移出正在执行的trigger
     */
    @Override
    public void run() {
        log.info("线程: {} 启动", this.getName());
        while (!isStop) {
            try {
                boolean hasMore = TesseractJobServiceDelegator.executorDetailService.clearInvalidMachine(tesseractGroup,
                        10, System.currentTimeMillis() - SCAN_INVALID_EXECUTOR_DETAIL_INTERVAL_TIME);
                if (hasMore) {
                    continue;
                }
            } catch (Exception e) {
                log.error("发生异常:{}", e.getMessage());
            }
            try {
                Thread.sleep(SCAN_INVALID_EXECUTOR_DETAIL_INTERVAL_TIME);
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
        log.info("线程: {} 启动", this.getName());
    }

    @Override
    public void stopThread() {
        log.info("线程: {} 停止", this.getName());
        this.isStop = true;
        this.interrupt();
    }

    @Override
    public void interruptThread() {
        this.interrupt();
    }
}
