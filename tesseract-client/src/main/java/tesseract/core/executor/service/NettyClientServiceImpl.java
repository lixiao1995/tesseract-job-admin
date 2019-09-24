package tesseract.core.executor.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.executor.ClientServiceDelegator;
import tesseract.core.util.HttpUtils;

import java.net.URI;

import static tesseract.core.executor.ClientServiceDelegator.serializerService;

/**
 * @description: netty 客户端
 * @author: nickle
 * @create: 2019-09-09 11:09
 **/
public class NettyClientServiceImpl implements IClientService {
    @Override
    public void registry(URI uri, TesseractAdminRegistryRequest request) throws InterruptedException {
        Channel channel = ClientServiceDelegator.nettyClient.getActiveChannel();
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, request)).sync();
    }

    @Override
    public void notify(URI uri, TesseractAdminJobNotify tesseractAdminJobNotify) throws InterruptedException {
        Channel channel = ClientServiceDelegator.nettyClient.getActiveChannel();
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, tesseractAdminJobNotify)).sync();
    }

    @Override
    public void heartbeat(URI uri, TesseractHeartbeatRequest heartBeatRequest) throws InterruptedException {
        Channel channel = ClientServiceDelegator.nettyClient.getActiveChannel();
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, heartBeatRequest)).sync();
    }

    private FullHttpRequest buildGeneralFullHttpRequest(URI uri, Object object) {
        byte[] serialize = serializerService.serialize(object);
        return HttpUtils.buildFullHttpRequest(uri, serialize, (fullHttpRequest) -> {
            fullHttpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        });
    }
}
