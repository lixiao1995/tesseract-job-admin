package admin.core.scheduler;

import admin.core.mail.TesseractMailSender;
import admin.core.netty.server.NettyServerCommandDispatcher;
import admin.core.TesseractJobServiceDelegator;
import admin.core.scheduler.bean.ScheduleGroupInfo;
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
import tesseract.core.netty.NettyServer;
import tesseract.core.serializer.ISerializerService;
import tesseract.exception.TesseractException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static admin.core.scheduler.TesseractBeanFactory.createScheduleGroupInfo;

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
    @Qualifier("mailEventBus")
    private EventBus mailEventBus;

    @Value("${tesseract.netty.server.port}")
    private int port;


    private static TesseractScheduleBoot tesseractScheduleBoot;

    /**
     * threadlist
     */

    private static final Map<String, ScheduleGroupInfo> SCHEDULE_GROUP_INFO_MAP = Maps.newHashMap();

    private static final ReentrantReadWriteLock REENTRANT_READ_WRITE_LOCK = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock READ_LOCK = REENTRANT_READ_WRITE_LOCK.readLock();
    private static final ReentrantReadWriteLock.WriteLock WRITE_LOCK = REENTRANT_READ_WRITE_LOCK.writeLock();


    /***********************************初始化 start*************************************/

    public void init() {
        tesseractScheduleBoot = this;
        initServiceDelegator();
        initGroupScheduler();
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
        TesseractJobServiceDelegator.mailEventBus = mailEventBus;
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
        if (!CollectionUtils.isEmpty(groupList)) {
            for (TesseractGroup group : groupList) {
                if (group.getThreadPoolNum() == 0) {
                    continue;
                }
                String groupName = group.getName();
                ScheduleGroupInfo scheduleGroupInfo = createScheduleGroupInfo(group);
                SCHEDULE_GROUP_INFO_MAP.put(groupName, scheduleGroupInfo);
            }
            log.info("组调度器初始化完成:{}", SCHEDULE_GROUP_INFO_MAP);
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
        log.info("调度线程停止");
        SCHEDULE_GROUP_INFO_MAP.values().forEach(scheduleGroupInfo -> {
            scheduleGroupInfo.stopThreadGroup();
        });
    }

    /**
     * spring 容器刷新后操作
     */
    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        log.info("调度线程开始");
        SCHEDULE_GROUP_INFO_MAP.values().forEach(scheduleGroupInfo -> {
            scheduleGroupInfo.startThreadGroup();
        });
        //启动netty server
        new Thread(() -> new NettyServer(port, new NettyServerCommandDispatcher()).startServer()).start();
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
            ScheduleGroupInfo scheduleGroupInfo = SCHEDULE_GROUP_INFO_MAP.remove(tesseractGroup.getName());
            scheduleGroupInfo.stopThreadGroup();
        } finally {
            WRITE_LOCK.unlock();
        }
        log.info("删除组调度器{}成功,删除结果:{}", tesseractGroup, SCHEDULE_GROUP_INFO_MAP);
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
            ScheduleGroupInfo scheduleGroupInfo = createScheduleGroupInfo(tesseractGroup);
            SCHEDULE_GROUP_INFO_MAP.put(groupName, scheduleGroupInfo);
        } finally {
            WRITE_LOCK.unlock();
        }
        log.info("添加组调度器{}成功,添加结果:{}", tesseractGroup, SCHEDULE_GROUP_INFO_MAP);
    }

    /**
     * 执行触发器
     *
     * @param groupName
     * @param tesseractTriggerList
     */
    public static void executeTrigger(String groupName, List<TesseractTrigger> tesseractTriggerList) {
        READ_LOCK.lock();
        SchedulerThread schedulerThread = SCHEDULE_GROUP_INFO_MAP.get(groupName).getSchedulerThread();
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
        SchedulerThread schedulerThread = SCHEDULE_GROUP_INFO_MAP.get(groupName).getSchedulerThread();
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
