package admin.core.scanner;

import admin.service.ITesseractExecutorDetailService;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.lifecycle.IThreadLifycycle;

/**
 * 失效执行器扫描线程
 *
 * @author nickle
 */
@Slf4j
public class ExecutorScanner extends Thread implements IThreadLifycycle {

    private ITesseractExecutorDetailService executorDetailService;


    private volatile boolean isStop = false;
    private Long scanIntervalTime = 15 * 1000L;
    private Long invalidTime = scanIntervalTime;


    public ExecutorScanner(ITesseractExecutorDetailService executorDetailService) {
        super("ExecutorScanner");
        this.executorDetailService = executorDetailService;
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
        log.info("ExecutorScanner start");
        while (!isStop) {
            try {
                boolean hasMore = executorDetailService.clearInvalidMachine(10, System.currentTimeMillis() - invalidTime);
                if (hasMore) {
                    continue;
                }
            } catch (Exception e) {
                log.error("发生异常:{}", e.getMessage());
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
