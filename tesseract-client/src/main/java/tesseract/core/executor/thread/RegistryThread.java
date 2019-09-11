package tesseract.core.executor.thread;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.dto.TesseractAdminJobDetailDTO;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.lifecycle.IThreadLifycycle;
import tesseract.service.IClientService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static tesseract.core.constant.CommonConstant.REGISTRY_MAPPING;

/**
 * 注册任务
 *
 * @author nickle
 */
@Slf4j
public class RegistryThread extends Thread implements IThreadLifycycle {
    private volatile boolean isStop = false;
    private IClientService clientFeignService;
    private List<ClientJobDetail> clientJobDetailList;
    private String adminServerAddress;
    private HeartbeatThread heartbeatThread;
    public volatile boolean isPause = false;

    public RegistryThread(IClientService clientFeignService, List<ClientJobDetail> clientJobDetailList, String adminServerAddress) {
        super("RegistryThread");
        this.clientFeignService = clientFeignService;
        this.clientJobDetailList = clientJobDetailList;
        this.adminServerAddress = adminServerAddress;
    }

    public void setHeartbeatThread(HeartbeatThread heartbeatThread) {
        this.heartbeatThread = heartbeatThread;
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
        log.info("RegistryThread start");
        while (!isStop) {
            if (!isPause) {
                //注册
                registry();
            }
            if (!isPause) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                }
                continue;
            }
            try {
                //注册成功后开始睡觉
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                log.info("中断");
            }
        }
    }

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
            clientFeignService.registry(new URI(adminServerAddress + REGISTRY_MAPPING), tesseractAdminRegistryRequest);
        } catch (Exception e) {
            e.printStackTrace();
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
        isPause = false;
        this.interrupt();
    }

    @Override
    public void pauseThread() {
        isPause = true;
    }
}