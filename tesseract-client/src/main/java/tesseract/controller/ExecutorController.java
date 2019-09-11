package tesseract.controller;

import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.TesseractExecutor;


public class ExecutorController {

    private TesseractExecutor tesseractExecutor;

    public TesseractExecutorResponse executeJob(TesseractExecutorRequest tesseractExecutorRequest) {
        return tesseractExecutor.execute(tesseractExecutorRequest);
    }

    public TesseractExecutorResponse exceptionHandler(Throwable throwable) {

        TesseractExecutorResponse fail = new TesseractExecutorResponse(TesseractExecutorResponse.FAIL_STAUTS, throwable.getMessage(), "");
        return fail;
    }
}
