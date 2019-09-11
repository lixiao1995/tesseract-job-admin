package tesseract.core.executor.thread;

import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.lifecycle.IThreadLifycycle;
import tesseract.service.IClientService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

import static tesseract.core.constant.CommonConstant.HEARTBEAT_MAPPING;

@Slf4j
public class HeartbeatThread extends Thread implements IThreadLifycycle {
    private volatile boolean isStop = false;
    private volatile boolean isPause = true;

    private IClientService clientFeignService;
    private String adminServerAddress;
    private RegistryThread registryThread;
    private Integer heartIntervalTime = 10 * 1000;
    private TesseractExecutor tesseractExecutor;

    public HeartbeatThread(IClientService clientFeignService, String adminServerAddress) {
        super("HeartbeatThread");
        this.clientFeignService = clientFeignService;
        this.adminServerAddress = adminServerAddress;
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
//            tesseractHeartbeatRequest.setSocket(String.format(SOCKET_FORMATTER, ip, port));
            clientFeignService.heartbeat(new URI(adminServerAddress + HEARTBEAT_MAPPING), tesseractHeartbeatRequest);
        } catch (URISyntaxException e) {
            log.error("uri信息错误，请检查配置");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("心跳失败:{}", e.getMessage());
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
