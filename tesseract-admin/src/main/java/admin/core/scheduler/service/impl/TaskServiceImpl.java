package admin.core.scheduler.service.impl;

import admin.core.TesseractJobServiceDelegator;
import admin.core.netty.server.handler.TesseractTaskExecutorHandler;
import admin.core.scheduler.TesseractFutureTask;
import admin.core.scheduler.service.ITaskService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.netty.NettyHttpClient;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.HttpUtils;
import tesseract.exception.TesseractException;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import static admin.core.TesseractJobServiceDelegator.CHANNEL_MAP;
import static admin.core.TesseractJobServiceDelegator.FUTURE_TASK_MAP;

/**
 * <p>Title TaskServiceImpl </p>
 * <p> </p>
 * <p>Company: http://www.koolearn.com </p>
 *
 * @author wangzhe01@Koolearn-inc.com
 * @date 2019/9/11 15:04
 */
@Slf4j
public class TaskServiceImpl implements ITaskService {

    @Override
    public TesseractExecutorResponse sendToExecutor(URI uri, TesseractExecutorRequest request) throws InterruptedException {
        TesseractExecutorResponse executorResponse;
        String socket = uri.getHost() + ":" + uri.getPort();
        try {
            NettyHttpClient nettyHttpClient = CHANNEL_MAP.get(socket);
            if (nettyHttpClient == null) {
                nettyHttpClient = new NettyHttpClient(uri.getHost(), uri.getPort(), new TesseractTaskExecutorHandler(socket, request.getExecutorDetailId()));
                CHANNEL_MAP.put(socket, nettyHttpClient);
            }
            Channel channel = nettyHttpClient.getActiveChannel();
            // 发送调度请求
            ISerializerService serializerService = TesseractJobServiceDelegator.serializerService;
            byte[] serialize = serializerService.serialize(request);
            FullHttpRequest httpRequest = HttpUtils.buildDefaultFullHttpRequest(uri, serialize);
            channel.writeAndFlush(httpRequest).sync();
            TesseractFutureTask<TesseractExecutorResponse> futureTask = new TesseractFutureTask<>();
            //同步等待客户端接收到任务回复信息
            FUTURE_TASK_MAP.put(request.getFireJobId(), futureTask);
            futureTask.lock();
            executorResponse = futureTask.get(2, TimeUnit.SECONDS);
            if (executorResponse == null) {
                throw new TesseractException("客户端响应超时");
            }
        } catch (Exception e) {
            CHANNEL_MAP.remove(socket);
            throw e;
        } finally {
            FUTURE_TASK_MAP.remove(request.getFireJobId());
        }
        return executorResponse;
    }

    @Override
    public void errorHandle(String socket) {
    }

}
