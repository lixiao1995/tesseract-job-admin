package com.kevin.communication.core.session;

import com.kevin.communication.core.context.Global;
import com.kevin.communication.core.server.IMessageProcessor;
import com.kevin.communication.core.tcp.AsyncMessageInvoker;
import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.enums.DeviceStatus;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.message.HeartBeatMessage;
import com.kevin.message.protocol.message.StatusMessage;
import com.kevin.message.protocol.utility.ProtocolHelper;
import com.kevin.communication.core.context.BeatContext;
import com.kevin.communication.core.context.ServerType;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 消息处理（socket 消息 处理器）
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:05
 */
public class SocketMessageProcessor implements IMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketMessageProcessor.class);

    //private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HB", CharsetUtil.UTF_8));

    @Override
    public void processReceiveHeartMessage(ChannelHandlerContext ctx, HeartBeatMessage msg) {
        Session session = SessionManager.getInstance().getSession(ctx);
        if (session == null) {
            //注册
            this.processReceiveConnect(ctx, msg);
        } else {
            //保持连接
            this.processReceivePing(ctx);
        }
    }

    /**
     * 处理心跳消息的连接请求
     *
     * @param ctx - ChannelHandlerContext
     * @param msg - IMessage
     */
    private void processReceiveConnect(ChannelHandlerContext ctx, HeartBeatMessage msg) {
        LOGGER.info("deviceId {} register!", msg.getDeviceId());

        Session session = SessionManager.getInstance().createSession(ctx, msg);
        SerializeType serializeType = Global.getInstance().getServiceConfig().getSerializeType();
        //发送心跳回应
        ctx.writeAndFlush(ProtocolHelper.createHeartBeatMessage(MessageFromType.SERVER, session.getDeviceId(), true,serializeType)).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    /**
     * 处理心跳消息的ping请求
     *
     * @param ctx - ChannelHandlerContext
     */
    private void processReceivePing(ChannelHandlerContext ctx) {
        Session session = SessionManager.getInstance().getSession(ctx);

        if (session != null) {
            LOGGER.info("deviceId {} keep register!", session.getDeviceId());

            boolean keepAlive = SessionManager.getInstance().keepSession(ctx);
            if (!keepAlive) {
                processReceiveDisconnect(ctx);
            } else {
                SerializeType serializeType = Global.getInstance().getServiceConfig().getSerializeType();
                ctx.writeAndFlush(ProtocolHelper.createHeartBeatMessage(MessageFromType.SERVER, session.getDeviceId(), false,serializeType)).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }
    }

    @Override
    public void processReceiveDisconnect(ChannelHandlerContext ctx) {
        Session session = SessionManager.getInstance().getSession(ctx);
        if (session != null) {
            LOGGER.info("deviceId {} unregister!", session.getDeviceId());
            //清除session状态
            session.close();
        } else {
            ctx.close();
        }
    }

    @Override
    public void processReceiveMessage(ChannelHandlerContext ctx, Protocol p) {
        Session session = SessionManager.getInstance().getSession(ctx);

        if (session == null || session.isClosed()) {
            LOGGER.warn("publish message's session is null or closed");
            return;
        }
        //组装BeatContext
        AsyncMessageInvoker.getInstance().invoke(BeatContext.wrapContext(p, session, ServerType.TCP));
    }

    @Override
    public void processStatusMessage(ChannelHandlerContext ctx, StatusMessage message) {
        Session session = SessionManager.getInstance().getSession(ctx);

        if (session == null || session.isClosed()) {
            LOGGER.warn("status message's session is null or closed!");
            return;
        }

        DeviceStatus status = DeviceStatus.getDeviceState(message.getStatus());

        LOGGER.info("from client message - deviceId: " + session.getDeviceId() + ",current status " + session.getDeviceStatus().name() + ",change status : " + status.name());

        //修改设备状态
        session.setDeviceStatus(status);
    }

}
