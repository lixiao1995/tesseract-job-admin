package tesseract.core.executor.thread;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.dto.TesseractAdminJobDetailDTO;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.executor.TesseractExecutor;
import tesseract.core.lifecycle.IThreadLifycycle;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static tesseract.core.constant.CommonConstant.HEARTBEAT_MAPPING;
import static tesseract.core.constant.CommonConstant.REGISTRY_MAPPING;
import static tesseract.core.executor.ClientServiceDelegator.clientJobDetailList;

/**
 * 合并心跳和注册操作线程
 *
 * @author nickle
 */
@Slf4j
public class PingPongThread extends Thread implements IThreadLifycycle {
    /**
     * 判断是否关闭
     */
    private volatile boolean isStop = false;
    /**
     * 判断是否是心跳操作，注册成功后设置为true，断开连接后设置为false
     */
    private volatile boolean isHeartBeat = false;
    /**
     * 线程睡眠时间，用于心跳
     */
    private final long sleepTime = 3000;

    private TesseractExecutor tesseractExecutor;

    public PingPongThread(TesseractExecutor tesseractExecutor) {
        super("PingPongThread");
        this.tesseractExecutor = tesseractExecutor;
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
        log.info("PingPongThread start");
        while (!isStop) {
            if (!isHeartBeat) {
                //注册
                registry();
            } else {
                //心跳
                heartbeat();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.info("中断");
            }
        }
    }

    /**
     * 心跳操作
     */
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
            ClientServiceDelegator.clientFeignService.heartbeat(new
                    URI(ClientServiceDelegator.adminServerAddress + HEARTBEAT_MAPPING), tesseractHeartbeatRequest);
        } catch (Exception e) {
            log.error("心跳失败:{},将开始重新注册", e.getMessage());
            //设置为 false 开启注册线程
            this.startRegistry();
        }
    }

    /**
     * 注册操作
     */
    private void registry() {
        try {
            log.info("开始注册");
            if (CollectionUtils.isEmpty(clientJobDetailList)) {
                log.info("clientJobDetailList 为空，注册停止");
                isStop = true;
                return;
            }
            TesseractAdminRegistryRequest tesseractAdminRegistryRequest = buildRequest();
            log.info("注册中:{}", tesseractAdminRegistryRequest);
            ClientServiceDelegator.clientFeignService.registry(
                    new URI(ClientServiceDelegator.adminServerAddress + REGISTRY_MAPPING), tesseractAdminRegistryRequest);
        } catch (Exception e) {
            log.error("注册失败:{}", e.getMessage());
        }
    }

    /**
     * 构建请求
     *
     * @return
     */
    private TesseractAdminRegistryRequest buildRequest() {
        TesseractAdminRegistryRequest tesseractAdminRegistryRequest = new TesseractAdminRegistryRequest();
        tesseractAdminRegistryRequest.setPort(ClientServiceDelegator.nettyServerPort);
        List<TesseractAdminJobDetailDTO> detailDTOList = Collections.synchronizedList(Lists.newArrayList());
        if (!CollectionUtils.isEmpty(clientJobDetailList)) {
            clientJobDetailList.parallelStream().forEach(clientJobDetail -> {
                TesseractAdminJobDetailDTO tesseractAdminJobDetailDTO = new TesseractAdminJobDetailDTO();
                tesseractAdminJobDetailDTO.setClassName(clientJobDetail.getClassName());
                tesseractAdminJobDetailDTO.setTriggerName(clientJobDetail.getTriggerName());
                detailDTOList.add(tesseractAdminJobDetailDTO);
            });
        }
        tesseractAdminRegistryRequest.setTesseractAdminJobDetailDTOList(detailDTOList);
        return tesseractAdminRegistryRequest;
    }

    /**
     * 将它从睡眠中唤醒注册
     */
    @Override
    public void interruptThread() {
        this.interrupt();
    }

    @Override
    public void pauseThread() {

    }

    public void startHeartBeat() {
        //设置为开始心跳
        this.isHeartBeat = true;
    }

    public void startRegistry() {
        //设置为开始心跳
        this.isHeartBeat = false;
    }
}