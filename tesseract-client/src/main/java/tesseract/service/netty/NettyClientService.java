package tesseract.service.netty;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.AllArgsConstructor;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractAdminRegistryRequest;
import tesseract.core.dto.TesseractHeartbeatRequest;
import tesseract.core.serializer.ISerializerService;
import tesseract.core.util.HttpUtils;
import tesseract.service.IClientService;

import java.net.URI;

/**
 * @description: netty 客户端
 * @author: nickle
 * @create: 2019-09-09 11:09
 **/
@AllArgsConstructor
public class NettyClientService implements IClientService {
    private ISerializerService serializerService;

    @Override
    public void registry(URI uri, TesseractAdminRegistryRequest request) throws InterruptedException {
        Channel channel = NettyHttpClient.getChannel(uri.getHost(), uri.getPort());
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, request)).sync();
    }

    @Override
    public void notify(URI uri, TesseractAdminJobNotify tesseractAdminJobNotify) throws InterruptedException {
        Channel channel = NettyHttpClient.getChannel(uri.getHost(), uri.getPort());
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, tesseractAdminJobNotify)).sync();
    }

    @Override
    public void heartbeat(URI uri, TesseractHeartbeatRequest heartBeatRequest) throws InterruptedException {
        Channel channel = NettyHttpClient.getChannel(uri.getHost(), uri.getPort());
        channel.writeAndFlush(buildGeneralFullHttpRequest(uri, heartBeatRequest)).sync();
    }

    private FullHttpRequest buildGeneralFullHttpRequest(URI uri, Object object) {
        byte[] serialize = serializerService.serialize(object);
        FullHttpRequest httpRequest = HttpUtils.buildFullHttpRequest(uri, serialize, (fullHttpRequest) -> {
            fullHttpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            fullHttpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
        });
        return httpRequest;
    }
}
