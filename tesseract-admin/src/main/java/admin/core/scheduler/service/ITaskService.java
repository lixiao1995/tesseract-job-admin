package admin.core.scheduler.service;

import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author nickle
 * @decription 任务执行服务，发送调度任务给执行器
 */
public interface ITaskService {
    /**
     * 通知机器执行任务
     *
     * @param uri
     * @param request
     * @return
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    TesseractExecutorResponse sendToExecutor(URI uri, TesseractExecutorRequest request) throws URISyntaxException, InterruptedException;

    /**
     * 发生异常处理器，目前没有使用
     *
     * @param socket
     */
    @Deprecated
    void errorHandle(String socket);
}
