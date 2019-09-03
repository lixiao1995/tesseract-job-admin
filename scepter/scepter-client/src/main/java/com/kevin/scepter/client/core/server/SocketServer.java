package com.kevin.scepter.client.core.server;

import com.kevin.message.protocol.ProtocolConst;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.message.MessageDecoder;
import com.kevin.message.protocol.message.MessageEncoder;
import com.kevin.message.protocol.utility.ProtocolHelper;
import com.kevin.scepter.client.core.config.SocketClientConfig;
import com.kevin.scepter.client.core.context.Global;
import com.kevin.scepter.client.core.message.IMessageProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * @author: kevin
 * @description: 通信服务
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:56
 */
public final class SocketServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketServer.class);

    private EventLoopGroup eventLoopGroup;

    private SocketClientConfig config;

    private Bootstrap bootstrap;

    private volatile boolean reConnection = false;

    public SocketServer() {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.config = Global.getInstance().getClientConfig();
    }

    public void start() throws InterruptedException {
        LOGGER.info("----------------start socket server--------------------");
        initServer();
        LOGGER.info("----------------socket server start finish-------------");
    }

    private void initServer() throws InterruptedException {
        // 客户端引导类
        bootstrap = new Bootstrap();
        // 多线程处理
        bootstrap.group(this.eventLoopGroup);
        // 指定通道类型为NioServerSocketChannel，一种异步模式，OIO阻塞模式为OioServerSocketChannel
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        //禁用Nagle算法
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        //连接超时
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.SO_TIMEOUT, 5000);
        // 指定请求地址
        bootstrap.remoteAddress(new InetSocketAddress(config.getServerHost(), config.getServerPort()));
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                IMessageProcessor messageProcessor = new SocketMessageProcessor();

                ChannelInboundHandlerAdapter clientHeartBeatHandler = Global.getInstance().getClientConfig().getClientHeartBeatHandler();
                if (clientHeartBeatHandler == null) {
                    clientHeartBeatHandler = new ClientHeartBeatHandler();
                }

                pipeline.addLast(new DelimiterBasedFrameDecoder(ProtocolConst.MAX_FRAME_LENGTH, true, Unpooled.copiedBuffer(ProtocolConst.P_END_TAG)));
                pipeline.addLast(new MessageDecoder());
                pipeline.addLast(new MessageEncoder());
                pipeline.addLast(new ConnectionWatchdog(SocketServer.this));
                pipeline.addLast(new IdleStateHandler(0, config.getWriterIdleTime(), 0, TimeUnit.SECONDS));//每隔30s的时间触发一次userEventTriggered的方法，并且指定IdleState的状态位是WRITER_IDLE
                pipeline.addLast(clientHeartBeatHandler);//实现userEventTriggered方法，并在state是WRITER_IDLE的时候发送一个心跳包到sever端，告诉server端我还活着
                pipeline.addLast(new ClientMessageHandler(messageProcessor));
            }
        });
//        LOGGER.info("端口为：" + config.getClientPort());
        ChannelFuture f = bootstrap.connect();
        f.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                LOGGER.info("connection server status : " + future.isDone() + ",success:" + future.isSuccess());

                reConnection = false;
                if (future.isDone() && future.isSuccess()) {
                    LOGGER.info("---- connection success-------");
                    SerializeType serializeType = Global.getInstance().getClientConfig().getSerializeType();
                    //服务器连接成功，立马发送一条心跳消息
                    future.channel().writeAndFlush(ProtocolHelper.createHeartBeatMessage(MessageFromType.CLIENT, config.getDeviceId(), true,serializeType));
                } else {
                    future.cause().printStackTrace();
                    try {
                        f.channel().close();
                    } catch (Exception e) {
                        LOGGER.info("=================");
                    }
                    LOGGER.info("---- connection fail," + config.getReconnectTime() + " second later reconnection ---------host=" + config.getServerHost() + ",port=" + config.getServerPort());
                    reconnection();
                }
            }

        });
    }

    /**
     * 重新连接，暂停的时间
     *
     * @throws InterruptedException
     */
    public synchronized void reconnection() throws InterruptedException {
        if (reConnection) {
            return;
        }
        reConnection = true;
        this.eventLoopGroup.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    initServer();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

        }, config.getReconnectTime(), TimeUnit.SECONDS);
    }

}
