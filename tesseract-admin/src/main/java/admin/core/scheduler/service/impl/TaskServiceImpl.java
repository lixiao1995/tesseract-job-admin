package admin.core.scheduler.service.impl;

import admin.core.netty.server.TesseractJobServiceDelegator;
import admin.core.netty.server.handler.TesseractTaskExecutorHandler;
import admin.core.scheduler.service.ITaskService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.netty.NettyClient;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.HttpUtils;

import java.net.URI;

import static admin.core.netty.server.TesseractJobServiceDelegator.CHANNEL_MAP;

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
        String socket = uri.getHost() + ":" + uri.getPort();
        NettyClient nettyClient = CHANNEL_MAP.get(socket);
        if (nettyClient == null) {
            nettyClient = new NettyClient(uri.getHost(), uri.getPort(), new TesseractTaskExecutorHandler(socket, request.getExecutorDetailId()));
            CHANNEL_MAP.put(socket, nettyClient);
        }
        Channel channel = nettyClient.getActiveChannel();
        // 发送调度请求
        ISerializerService serializerService = TesseractJobServiceDelegator.serializerService;
        byte[] serialize = serializerService.serialize(request);
        FullHttpRequest httpRequest = HttpUtils.buildFullHttpRequest(uri, serialize, (fullHttpRequest) -> {
            fullHttpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        });
        channel.writeAndFlush(httpRequest).sync();
        return TesseractExecutorResponse.SUCCESS;
    }

    @Override
    public void errorHandle(String socket) {
    }

}
