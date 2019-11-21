package admin.core.scheduler;

import lombok.Data;
import tesseract.exception.TesseractException;

import java.util.concurrent.*;

/**
 * @description: 同步等待任务完成
 * @author: nickle
 * @create: 2019-10-16 10:14
 **/
@Data
public class TesseractFutureTask<T> implements Future<T> {
    private T t;
    private Semaphore semaphore = new Semaphore(1);

    public void lock() throws InterruptedException {
        semaphore.acquire();
    }

    public void unlock() {
        semaphore.release();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new TesseractException("不支持的操作");
    }

    @Override
    public boolean isCancelled() {
        throw new TesseractException("不支持的操作");
    }

    @Override
    public boolean isDone() {
        throw new TesseractException("不支持的操作");
    }

    @Override
    public T get() throws InterruptedException {
        semaphore.acquire();
        semaphore.release();
        return t;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        semaphore.tryAcquire(timeout, unit);
        semaphore.release();
        return t;
    }
}
