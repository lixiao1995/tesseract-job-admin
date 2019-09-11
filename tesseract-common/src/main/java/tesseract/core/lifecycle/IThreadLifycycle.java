package tesseract.core.lifecycle;

/**
 * @author nickle
 */
public interface IThreadLifycycle {
    default void initThread() {
    }

    default void startThread() {
    }

    default void stopThread() {
    }

    default void pauseThread() {
    }

    default void interruptThread() {
    }
}
