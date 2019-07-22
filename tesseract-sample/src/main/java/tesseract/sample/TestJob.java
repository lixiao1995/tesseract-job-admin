package tesseract.sample;

import org.springframework.stereotype.Component;
import tesseract.core.annotation.TesseractJob;
import tesseract.core.context.ExecutorContext;
import tesseract.core.handler.JobHandler;

import java.util.concurrent.atomic.AtomicInteger;

@TesseractJob(triggerName = "testTrigger")
@Component
public class TestJob implements JobHandler {


    private AtomicInteger num = new AtomicInteger(0);


    @Override
    public void execute(ExecutorContext executorContext) throws Exception {
        System.out.println("任务执行开始");
        Thread.sleep(30 * 1000);
        int i = num.incrementAndGet();
        if (i % 10 == 1) {
            throw new Exception("测试异常");
        }
        System.out.println("任务执行结束,第" + i + "次");
    }
}
