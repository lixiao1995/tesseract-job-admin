package admin.core.scheduler.pool;

/**
 * 〈执行线程线程池〉
 *
 * @author nickel
 * @create 2019/6/23
 * @since 1.0.0
 */
public interface ISchedulerThreadPool {
    /**
     * 获取可用线程数
     *
     * @return
     */
    int blockGetAvailableThreadNum();

    /**
     * 执行job
     *
     * @param runnable
     */
    void runJob(Runnable runnable);

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 初始化
     */
    void init();

    /**
     * 改变大小
     *
     * @param threadNum
     */
    void changeSize(Integer threadNum);
}
