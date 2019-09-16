package admin.core.scheduler;

import admin.core.component.SenderDelegateBuilder;
import admin.core.netty.server.NettyServer;
import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.core.scanner.ExecutorScanner;
import admin.core.scanner.MissfireScanner;
import admin.core.scheduler.pool.DefaultSchedulerThreadPool;
import admin.core.scheduler.pool.ISchedulerThreadPool;
import admin.entity.TesseractGroup;
import admin.entity.TesseractTrigger;
import admin.service.*;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;
import tesseract.core.serializer.ISerializerService;
import tesseract.exception.TesseractException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class TesseractScheduleBoot {
    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    @Autowired
    private ITesseractExecutorDetailService tesseractExecutorDetailService;

    @Autowired
    private ITesseractJobDetailService tesseractJobDetailService;

    @Autowired
    private ITesseractExecutorService tesseractExecutorService;

    @Autowired
    private ITesseractGroupService tesseractGroupService;

    @Autowired
    private ITesseractLogService tesseractLogService;

    @Autowired
    private SenderDelegateBuilder senderDelegateBuilder;

    @Autowired
    private ISerializerService serializerService;

    @Autowired
    @Qualifier("retryEventBus")
    EventBus retryEventBus;

    @Value("${netty.port}")
    private int port;


    private static TesseractScheduleBoot tesseractScheduleBoot;

    /**
     * threadlist
     */

    private static final Map<String, SchedulerThread> SCHEDULER_THREAD_MAP = Maps.newHashMap();

    private static final ReentrantReadWriteLock REENTRANT_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = REENTRANT_READ_WRITE_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = REENTRANT_READ_WRITE_LOCK.writeLock();
    /**
     * 暂时先共用同一扫描器
     */
    private ExecutorScanner executorScanner;
    private MissfireScanner missfireScanner;

    /**
     * 单线程 不需要加锁
     */
    public void destroy() {
        SCHEDULER_THREAD_MAP.values().forEach(schedulerThread -> {
            schedulerThread.stopThread();
            schedulerThread.getTesseractTriggerDispatcher().stop();
        });

        if (executorScanner != null) {
            executorScanner.stopThread();
        }

        if (missfireScanner != null) {
            missfireScanner.stopThread();
        }
    }

    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        SCHEDULER_THREAD_MAP.values().forEach(SchedulerThread::startThread);
        if (executorScanner != null) {
            executorScanner.startThread();
        }
        if (missfireScanner != null) {
            missfireScanner.startThread();
        }
        //启动netty server
        new Thread(() -> {
            new NettyServer().start(port);
        }).start();

    }

    /***********************************初始化*************************************/

    public void init() {
        tesseractScheduleBoot = this;
        initGroupScheduler();
        initServiceDelegator();
    }

    /**
     * 初始化service 代理
     */
    private void initServiceDelegator() {
        Map<Class, Object> tesseractJobServiceMap = TesseractJobServiceDelegator.TESSERACT_JOB_SERVICE_MAP;
        tesseractJobServiceMap.put(ITesseractTriggerService.class, tesseractTriggerService);
        tesseractJobServiceMap.put(ITesseractExecutorDetailService.class, tesseractExecutorDetailService);
        tesseractJobServiceMap.put(ITesseractJobDetailService.class, tesseractJobDetailService);
        tesseractJobServiceMap.put(ITesseractExecutorService.class, tesseractExecutorService);
        tesseractJobServiceMap.put(ITesseractGroupService.class, tesseractGroupService);
        tesseractJobServiceMap.put(ISerializerService.class, serializerService);
        tesseractJobServiceMap.put(ITesseractLogService.class, tesseractLogService);
        tesseractJobServiceMap.put(ExecutorScanner.class, executorScanner);
    }

    /**
     * 初始化组调度器
     */
    private void initGroupScheduler() {
        //创建调度线程,根据部门进行线程池隔离
        List<TesseractGroup> groupList = tesseractGroupService.list();
        boolean hasScheduler = false;
        if (!CollectionUtils.isEmpty(groupList)) {
            for (TesseractGroup group : groupList) {
                //默认调度组不需要调度任何程序
                if (group.getThreadPoolNum() == 0) {
                    continue;
                }
                hasScheduler = true;
                String groupName = group.getName();
                SCHEDULER_THREAD_MAP.put(groupName, createSchedulerThread(group));
            }
            if (hasScheduler) {
                //创建扫描线程
                missfireScanner = new MissfireScanner(tesseractTriggerService);
                missfireScanner.setDaemon(true);
            }
            //失效机器扫描器
            executorScanner = new ExecutorScanner(tesseractExecutorDetailService);
            executorScanner.setDaemon(true);
            return;
        }
        log.warn("没有调度组");
    }

    /**
     * 创建调度线程
     *
     * @param tesseractGroup
     * @return
     */
    private SchedulerThread createSchedulerThread(TesseractGroup tesseractGroup) {
        SchedulerThread schedulerThread = new SchedulerThread(tesseractGroup, createTesseractTriggerDispatcher(tesseractGroup.getName(), tesseractGroup.getThreadPoolNum()), tesseractTriggerService);
        schedulerThread.setDaemon(true);
        return schedulerThread;
    }

    /**
     * 创建任务分发器
     *
     * @param groupName
     * @param threadNum
     * @return
     */
    private TesseractTriggerDispatcher createTesseractTriggerDispatcher(String groupName, Integer threadNum) {
        DefaultSchedulerThreadPool threadPool = new DefaultSchedulerThreadPool(threadNum);
        TesseractTriggerDispatcher tesseractTriggerDispatcher = new TesseractTriggerDispatcher();
        tesseractTriggerDispatcher.setGroupName(groupName);
        tesseractTriggerDispatcher.setExecutorDetailService(tesseractExecutorDetailService);
        tesseractTriggerDispatcher.setExecutorService(tesseractExecutorService);
        tesseractTriggerDispatcher.setTesseractJobDetailService(tesseractJobDetailService);
        tesseractTriggerDispatcher.setThreadPool(threadPool);
        tesseractTriggerDispatcher.setTaskExecutorDelegate(senderDelegateBuilder.getTaskExecutorDelegate());
        tesseractTriggerDispatcher.setRetryEventBus(retryEventBus);
        return tesseractTriggerDispatcher;
    }

    /***********************************静态工具方法*************************************/

    /**
     * 删除组线程池并停止
     *
     * @param
     */
    public static void deleteGroupScheduler(TesseractGroup tesseractGroup) {
        WRITE_LOCK.lock();
        try {
            SchedulerThread schedulerThread = SCHEDULER_THREAD_MAP.remove(tesseractGroup.getName());
            if (schedulerThread == null) {
                log.error("找不到组:{} SchedulerThread", tesseractGroup);
                throw new TesseractException("找不到SchedulerThread");
            }
            schedulerThread.stopThread();
            //如果没有线程组了停止扫描线程
            if (SCHEDULER_THREAD_MAP.size() == 0) {
                tesseractScheduleBoot.missfireScanner.stopThread();
                tesseractScheduleBoot.missfireScanner = null;
            }
        } finally {
            WRITE_LOCK.unlock();
        }
        log.info("删除组调度器{}成功,删除结果:{}", tesseractGroup, SCHEDULER_THREAD_MAP);
    }

    /**
     * 增加组线程池
     *
     * @param
     */
    public static void addGroupScheduler(TesseractGroup tesseractGroup) {
        WRITE_LOCK.lock();
        try {
            String groupName = tesseractGroup.getName();
            SchedulerThread schedulerThread = tesseractScheduleBoot.createSchedulerThread(tesseractGroup);
            schedulerThread.startThread();
            SCHEDULER_THREAD_MAP.put(groupName, schedulerThread);
            //检测scanner是否创建，如果只有一个默认调度组将不会创建
            if (tesseractScheduleBoot.missfireScanner == null) {
                tesseractScheduleBoot.missfireScanner = new MissfireScanner(tesseractScheduleBoot.tesseractTriggerService);
                tesseractScheduleBoot.missfireScanner.startThread();
            }
        } finally {
            WRITE_LOCK.unlock();
        }
        log.info("添加组调度器{}成功,添加结果:{}", tesseractGroup, SCHEDULER_THREAD_MAP);
    }

    /**
     * 执行触发器
     *
     * @param groupName
     * @param tesseractTriggerList
     */
    public static void executeTrigger(String groupName, List<TesseractTrigger> tesseractTriggerList) {
        READ_LOCK.lock();
        SchedulerThread schedulerThread = SCHEDULER_THREAD_MAP.get(groupName);
        try {
            if (schedulerThread == null) {
                log.error("找不到组:{} SchedulerThread", groupName);
                throw new TesseractException("找不到SchedulerThread");
            }
            TesseractTriggerDispatcher tesseractTriggerDispatcher = schedulerThread.getTesseractTriggerDispatcher();
            tesseractTriggerDispatcher.dispatchTrigger(tesseractTriggerList, true);
        } finally {
            READ_LOCK.unlock();
        }
    }

    /**
     * 更新执行线程池大小
     *
     * @param groupName
     * @param threadNum
     */
    public static void updateThreadNum(String groupName, Integer threadNum) {
        READ_LOCK.lock();
        SchedulerThread schedulerThread = SCHEDULER_THREAD_MAP.get(groupName);
        try {
            if (schedulerThread == null) {
                log.error("找不到组:{} SchedulerThread", groupName);
                throw new TesseractException("找不到SchedulerThread");
            }
            ISchedulerThreadPool threadPool = schedulerThread.getTesseractTriggerDispatcher().getThreadPool();
            threadPool.changeSize(threadNum);
        } finally {
            READ_LOCK.unlock();
        }
    }
}
