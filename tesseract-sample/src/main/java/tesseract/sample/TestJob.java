package tesseract.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tesseract.core.annotation.TesseractJob;
import tesseract.core.context.ExecutorContext;
import tesseract.core.handler.JobHandler;

@TesseractJob(triggerName = "testTrigger-1")
@Component
@Slf4j
public class TestJob implements JobHandler {


    @Override
    public void execute(ExecutorContext executorContext) throws Exception {
        System.out.println("开始任务");
        Thread.sleep(2 * 1000);
        System.out.println("任务结束");
    }
}