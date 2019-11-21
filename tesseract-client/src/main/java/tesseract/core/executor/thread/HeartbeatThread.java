package tesseract.core.executor.thread;

import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

import static tesseract.core.constant.CommonConstant.HEARTBEAT_MAPPING;

/**
 * 心跳处理线程
 *
 * @author nickle
 */
@Slf4j
public class HeartbeatThread extends Thread implements IThreadLifycycle {
    private volatile boolean isStop = false;
    private volatile boolean isPause = true;

    private RegistryThread registryThread;
    private Integer heartIntervalTime = 10 * 1000;
    private TesseractExecutor tesseractExecutor;

    public HeartbeatThread() {
        super("HeartbeatThread");
    }

    public void setTesseractExecutor(TesseractExecutor tesseractExecutor) {
        this.tesseractExecutor = tesseractExecutor;
    }

    public void setRegistryThread(RegistryThread registryThread) {
        this.registryThread = registryThread;
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
        isStop = true;
        this.interrupt();
    }

    @Override
    public void run() {
        log.info("HeartbeatThread start");
        while (!isStop) {
            if (isPause) {
                try {
                    /**
                     * 暂停情况下开始睡觉
                     */
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                }
                continue;
            }
            //开始心跳
            heartbeat();
            try {
                Thread.sleep(heartIntervalTime);
            } catch (InterruptedException e) {
            }
        }
    }

    private void heartbeat() {
        try {
            TesseractHeartbeatRequest tesseractHeartbeatRequest = new TesseractHeartbeatRequest();
            ThreadPoolExecutor threadPoolExecutor = tesseractExecutor.getThreadPoolExecutor();
            int activeCount = threadPoolExecutor.getActiveCount();
            int corePoolSize = threadPoolExecutor.getCorePoolSize();
            int maximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
            int poolSize = threadPoolExecutor.getPoolSize();
            int queueSize = threadPoolExecutor.getQueue().size();
            tesseractHeartbeatRequest.setActiveCount(activeCount);
            tesseractHeartbeatRequest.setCorePoolSize(corePoolSize);
            tesseractHeartbeatRequest.setMaximumPoolSize(maximumPoolSize);
            tesseractHeartbeatRequest.setPoolSize(poolSize);
            tesseractHeartbeatRequest.setQueueSize(queueSize);
            tesseractHeartbeatRequest.setPort(ClientServiceDelegator.nettyServerPort);
//            tesseractHeartbeatRequest.setSocket(String.format(SOCKET_FORMATTER, ip, port));
            ClientServiceDelegator.clientFeignService.heartbeat(new
                    URI(ClientServiceDelegator.adminServerAddress + HEARTBEAT_MAPPING), tesseractHeartbeatRequest);
        } catch (Exception e) {
            log.error("心跳失败:{},将开始重新注册", e.getMessage());
            registryThread.interruptThread();
            this.pauseThread();
        }
    }

    /**
     * 开始心跳
     */
    @Override
    public void interruptThread() {
        this.isPause = false;
        this.interrupt();
    }

    @Override
    public void pauseThread() {
        this.isPause = true;
    }
}
