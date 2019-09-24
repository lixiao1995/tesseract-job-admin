package tesseract.core.executor.service;

import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractHeartbeatRequest;

import java.net.URI;

/**
 * 用于与服务端通信
 *
 * @author nickle
 */
public interface IClientService {
    /**
     * 向服务端注册
     *
     * @param uri
     * @param request
     * @throws InterruptedException
     */
    void registry(URI uri, TesseractAdminRegistryRequest request) throws InterruptedException;

    /**
     * 执行任务状态通知
     *
     * @param uri
     * @param tesseractAdminJobNotify
     */
    void notify(URI uri, TesseractAdminJobNotify tesseractAdminJobNotify) throws InterruptedException;

    /**
     * 心跳
     *
     * @param uri
     * @param heartBeatRequest
     */
    void heartbeat(URI uri, TesseractHeartbeatRequest heartBeatRequest) throws InterruptedException;
}
