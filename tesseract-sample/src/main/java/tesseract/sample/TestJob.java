package tesseract.sample;

import org.springframework.stereotype.Component;
import tesseract.core.annotation.TesseractJob;
import tesseract.core.context.ExecutorContext;
import tesseract.core.handler.JobHandler;

@TesseractJob(triggerName = "testTrigger-1")
@Component
public class TestJob implements JobHandler {


    @Override
    public void execute(ExecutorContext executorContext) throws Exception {
        throw new Exception("添加失败任务，测试重试功能");
    }
}
