package admin.core.netty.server;

import admin.core.component.TesseractMailSender;
import admin.core.scheduler.scanner.ExecutorScanner;
import admin.core.scheduler.service.ITaskService;
import admin.service.*;
import com.google.common.eventbus.EventBus;
import io.netty.channel.Channel;
import tesseract.core.serializer.ISerializerService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 保存业务service，统一服务获取入口
 * @author: nickle
 * @create: 2019-09-09 10:17
 **/
public class TesseractJobServiceDelegator {

    public static final Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    public static ITesseractExecutorService executorService;

    public static ITesseractExecutorDetailService executorDetailService;

    public static ISerializerService serializerService;

    public static ITesseractTriggerService triggerService;

    public static ITesseractLogService logService;

    public static ITaskService taskService;

    public static ITesseractJobDetailService jobDetailService;

    public static ITesseractFiredJobService firedJobService;

    public static ITesseractGroupService groupService;

    public static ExecutorScanner executorScanner;

    public static EventBus mailEventBus;

    public static TesseractMailSender mailSender;
}
