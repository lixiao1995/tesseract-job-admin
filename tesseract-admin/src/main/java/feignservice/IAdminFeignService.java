package feignservice;

import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;

import java.net.URI;
import java.net.URISyntaxException;

public interface IAdminFeignService {
    TesseractExecutorResponse sendToExecutor(URI uri, TesseractExecutorRequest request) throws URISyntaxException, InterruptedException;

    void errorHandle(String socket);
}
