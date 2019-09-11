package tesseract.service.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import lombok.extern.slf4j.Slf4j;
import tesseract.core.serializer.ISerializerService;
import tesseract.exception.TesseractException;

@Slf4j
public class NettyHttpClient {
    private static volatile Channel channel;
    private static EventLoopGroup eventLoopGroup;
    public static ISerializerService serializerService;

    public static Channel getChannel(String host, int port) {
        if (channel == null || !channel.isActive()) {
            synchronized (NettyHttpClient.class) {
                if (channel == null || !channel.isActive()) {
                    try {
                        connect(host, port);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        throw new TesseractException("创建channel出错");
                    }
                }
            }
        }
        return channel;
    }

    public static void close() {
        if (channel.isActive()) {
            channel.close();
        }
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private static void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpResponseDecoder());
                ch.pipeline().addLast(new HttpRequestEncoder());
                ch.pipeline().addLast(new HttpObjectAggregator(5 * 1024));
                ch.pipeline().addLast(new NettyClientCommandDispatcher(serializerService));
            }
        });
        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
    }
}