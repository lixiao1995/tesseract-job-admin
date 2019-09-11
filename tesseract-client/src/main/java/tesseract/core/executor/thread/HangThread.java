package tesseract.core.executor.thread;

import lombok.extern.slf4j.Slf4j;
import tesseract.core.lifecycle.IThreadLifycycle;

@Slf4j
public class HangThread extends Thread implements IThreadLifycycle {
    private Object lock = new Object();

    @Override
    public void initThread() {

    }

    @Override
    public void startThread() {
        this.start();
    }

    @Override
    public void stopThread() {
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            log.info("executor 停止");
        }
    }
}
