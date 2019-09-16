package admin.core.scheduler;

import admin.core.component.TesseractMailSender;
import admin.core.netty.server.NettyServer;
import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.core.scanner.ExecutorScanner;
import admin.core.scanner.MissfireScanner;
import admin.core.scheduler.pool.ISchedulerThreadPool;
import admin.core.scheduler.service.ITaskService;
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

import static admin.core.scheduler.TesseractBeanFactory.createSchedulerThread;

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
    private ITesseractFiredJobService firedJobService;


    @Autowired
    private ITesseractLogService tesseractLogService;

    @Autowired
    private TesseractMailSender mailSender;

    @Autowired
    private ISerializerService serializerService;

    @Autowired
    private ITaskService taskService;

    @Autowired
    @Qualifier("retryEventBus")
    private EventBus retryEventBus;

    @Autowired
    @Qualifier("mailEventBus")
    private EventBus mailEventBus;

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


    /***********************************初始化 start*************************************/

    public void init() {
        tesseractScheduleBoot = this;
        initGroupScheduler();
        initServiceDelegator();
    }

    /**
     * 初始化service 代理
     */
    private void initServiceDelegator() {
        TesseractJobServiceDelegator.triggerService = tesseractTriggerService;
        TesseractJobServiceDelegator.jobDetailService = tesseractJobDetailService;
        TesseractJobServiceDelegator.executorService = tesseractExecutorService;
        TesseractJobServiceDelegator.executorDetailService = tesseractExecutorDetailService;
        TesseractJobServiceDelegator.groupService = tesseractGroupService;
        TesseractJobServiceDelegator.serializerService = serializerService;
        TesseractJobServiceDelegator.logService = tesseractLogService;
        TesseractJobServiceDelegator.executorScanner = executorScanner;
        TesseractJobServiceDelegator.mailEventBus = mailEventBus;
        TesseractJobServiceDelegator.retryEventBus = retryEventBus;
        TesseractJobServiceDelegator.firedJobService = firedJobService;
        TesseractJobServiceDelegator.taskService = taskService;
        TesseractJobServiceDelegator.mailSender = mailSender;
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

    /***********************************初始化 end*************************************/

    /***********************************spring 相关 start*************************************/
    /**
     * spring 容器关闭执行操作
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

    /**
     * spring 容器刷新后操作
     */
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
    /***********************************spring 相关 end*************************************/


    /***********************************静态工具方法  start *************************************/

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
            SchedulerThread schedulerThread = createSchedulerThread(tesseractGroup);
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
            tesseractTriggerDispatcher.dispatchTrigger(tesseractTriggerList);
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

    /***********************************静态工具方法  end *************************************/
}
