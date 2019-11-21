package tesseract.core.handler;

import tesseract.core.context.ExecutorContext;

/**
 * 任务执行体
 *
 * @author nickle
 */
@FunctionalInterface
public interface JobHandler {
    /**
     * 任务执行入口
     *
     * @param executorContext 服务端传递过来的上下文
     * @throws Exception
     */
    void execute(ExecutorContext executorContext) throws Exception;
}
