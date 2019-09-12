package admin.core.netty.server;

import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractExecutorService;
import admin.service.ITesseractLogService;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import tesseract.core.serializer.ISerializerService;
import tesseract.exception.TesseractException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @description: 业务service的代理
 * @author: nickle
 * @create: 2019-09-09 10:17
 **/
public class TesseractJobServiceDelegator {
    public static final Map<Class, Object> TESSERACT_JOB_SERVICE_MAP = Maps.newHashMap();

    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static ITesseractExecutorService getTesseractExecutorService() {
        return (ITesseractExecutorService) Optional.of(TESSERACT_JOB_SERVICE_MAP.get(ITesseractExecutorService.class)).orElseThrow(() -> new TesseractException("获取服务异常"));
    }

    public static ITesseractExecutorDetailService getTesseractExecutorDetailService() {
        return (ITesseractExecutorDetailService) Optional.of(TESSERACT_JOB_SERVICE_MAP.get(ITesseractExecutorDetailService.class)).orElseThrow(() -> new TesseractException("获取服务异常"));
    }

    public static ISerializerService getSerializerService() {
        return (ISerializerService) Optional.of(TESSERACT_JOB_SERVICE_MAP.get(ISerializerService.class)).orElseThrow(() -> new TesseractException("获取服务异常"));
    }

    public static ITesseractLogService getTesseractLogService() {
        return (ITesseractLogService) Optional.of(TESSERACT_JOB_SERVICE_MAP.get(ITesseractLogService.class)).orElseThrow(() -> new TesseractException("获取服务异常"));
    }

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }
}
