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
    TesseractExecutorResponse sendToExecutor(URI uri, TesseractExecutorRequest request) throws URISyntaxException, InterruptedException;

    void errorHandle(String socket);
}
