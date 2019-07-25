package admin.constant;

import admin.core.scheduler.router.IScheduleRouter;
import admin.core.scheduler.router.impl.HashRouter;
import admin.core.scheduler.router.impl.LoadFactorRouter;
import admin.core.scheduler.router.impl.PollingRouter;

import java.util.HashMap;
import java.util.Map;

import static admin.util.AdminUtils.bcryptEncode;

public class AdminConstant {
    /**
     * 用户相关
     */
    public static final Integer USER_VALID = 1;
    public static final Integer USER_INVALID = 0;
    public static final String SUPER_ADMIN_NAME = "super_admin";
    public static final String DEFAULT_PASSWORD = "666666";
    public static final String DEFAULT_PASSWORD_CODE = bcryptEncode(DEFAULT_PASSWORD);


    /**
     * 日志相关
     */
    public static final Integer LOG_NO_CONFIRM = 3;
    public static final Integer LOG_WAIT = 2;
    public static final Integer LOG_SUCCESS = 1;
    public static final Integer LOG_FAIL = 0;
    /**
     * 锁相关
     */
    public static final String TRIGGER_LOCK_NAME = "TRIGGER_LOCK";
    public static final String JOB_LOCK_NAME = "JOB_LOCK";
    public static final String MISSFIRE_LOCK_NAME = "MISSFIRE_LOCK_NAME";
    public static final String EXECUTOR_LOCK_NAME = "EXECUTOR_LOCK_NAME";
    /**
     * 调度策略
     */
    public static final Integer SCHEDULER_STRATEGY_HASH = 0;
    public static final Integer SCHEDULER_STRATEGY_POLLING = 1;
    public static final Integer SCHEDULER_STRATEGY_LOADFACTOR = 2;
    public static final Integer SCHEDULER_STRATEGY_BROADCAST = 3;
    public static final Integer SCHEDULER_STRATEGY_SHARDING = 4;

    public static final Map<Integer, String> SCHEDULER_NAME_MAP = new HashMap<Integer, String>() {
        {
            put(SCHEDULER_STRATEGY_HASH, "hash");
            put(SCHEDULER_STRATEGY_POLLING, "轮询");
            put(SCHEDULER_STRATEGY_LOADFACTOR, "负载均衡");
            put(SCHEDULER_STRATEGY_BROADCAST, "广播");
            put(SCHEDULER_STRATEGY_SHARDING, "分片");
        }
    };

    public static final Map<Integer, IScheduleRouter> SCHEDULE_ROUTER_MAP = new HashMap<Integer, IScheduleRouter>() {
        {
            put(SCHEDULER_STRATEGY_HASH, new HashRouter());
            put(SCHEDULER_STRATEGY_POLLING, new PollingRouter());
            put(SCHEDULER_STRATEGY_LOADFACTOR, new LoadFactorRouter());
        }
    };
    /**
     * 触发器状态
     */
    public static final Integer TRGGER_STATUS_STOPING = 0;
    public static final Integer TRGGER_STATUS_STARTING = 1;

    /**
     * 安全相关
     */
    public static final String TOKEN = "X-Token";
    public static final String ANONYMOUS_TAG = "anonymousUser";

    /**
     * 菜单管理
     */
    public static final Integer MENU_NO_CACHE = 0;
    public static final Integer MENU_CACHE = 1;
}
