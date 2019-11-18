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
    private static long count;

    @Override
    public void execute(ExecutorContext executorContext) throws Exception {
        System.out.println("开始任务");
        Thread.sleep(2 * 1000);
        count++;
        if (count % 10 == 0) {
            throw new RuntimeException();
        }
        System.out.println("任务结束");
    }
}