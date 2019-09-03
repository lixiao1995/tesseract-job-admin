package com.kevin.scepter.client.core.session;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.enums.DeviceStatus;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.exception.TimeoutException;
import com.kevin.message.protocol.message.IMessage;
import com.kevin.message.protocol.utility.ProtocolHelper;
import com.kevin.scepter.client.core.context.Global;
import com.kevin.scepter.client.core.hotkey.IAsyncCallBackListener;
import com.kevin.scepter.client.core.hotkey.IReturnHandlerListener;
import com.kevin.scepter.client.core.message.AsyncMessageProcessor;
import com.kevin.scepter.client.core.message.AutoReceiveEvent;
import com.kevin.scepter.client.core.message.MessageIdFactory;
import com.kevin.scepter.client.core.message.MessageWaitProcessor;
import com.kevin.scepter.client.core.message.WindowData;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author: kevin
 * @description: 默认缓存
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:57
 */
public class DefaultSession implements ISession {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSession.class);

    private volatile boolean closed = false;

    private ChannelHandlerContext ctx;

    private String deviceId;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    private long requestTimeout;

    public DefaultSession(ChannelHandlerContext ctx, String deviceId) {
        this.ctx = ctx;
        this.deviceId = deviceId;
        this.requestTimeout = Global.getInstance().getClientConfig().getRequestTimeout();
        //默认空闲状态
        this.status = DeviceStatus.IDLE;
    }

    @Override
    public IMessage request(Protocol p) throws IOException, TimeoutException, Exception {
        int messageId = p.getMessageId();
        if (messageId <= 0) {
            messageId = MessageIdFactory.createMessageId();
            p.setMessageId(messageId);
        }

        try {
            MessageWaitProcessor.registerEvent(messageId);
            ctx.writeAndFlush(p);
        } catch (Throwable ex) {
            LOGGER.error("client get socket Exception", ex);
            //抛出异常了则移除定时器
            MessageWaitProcessor.unregisterEvent(messageId);
            throw ex;
        }

        WindowData wd = MessageWaitProcessor.getWindowData(messageId);
        if (wd != null) {
            try {
                AutoReceiveEvent event = wd.getEvent();
                if (!event.waitOne(this.requestTimeout)) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("ServerIP:[").append(Global.getInstance().getClientConfig().getServerHost());
                    buf.append("],ServerPort:[").append(Global.getInstance().getClientConfig().getServerHost());
                    buf.append("],Receive data timeout or error!timeout:").append(this.requestTimeout).append("s");

                    throw new TimeoutException(buf.toString());
                }
                return (IMessage) wd.getProtocol().getEntity();
            } finally {//移除定时
                MessageWaitProcessor.unregisterEvent(messageId);
            }
        }

        return null;
    }

    @Override
    public void requestAsync(Protocol p, IReturnHandlerListener returnHandler, IAsyncCallBackListener[] callBacks, boolean future) throws IOException, TimeoutException, Exception {
        int messageId = p.getMessageId();
        if (messageId <= 0) {
            messageId = MessageIdFactory.createMessageId();
            p.setMessageId(messageId);
        }

        try {
            WindowData wd = new WindowData(messageId, p, returnHandler, callBacks, future);
            MessageWaitProcessor.registerEvent(messageId, wd);
            //添加到异步回调执行队列中
            AsyncMessageProcessor.offerSendQueue(wd);
        } catch (Throwable ex) {
            LOGGER.error("client get socket Exception", ex);
            //抛出异常了则移除定时器
            MessageWaitProcessor.unregisterEvent(messageId);
            throw ex;
        }
    }

    @Override
    public void receive(Protocol p) throws IOException, TimeoutException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public DeviceStatus getDeviceStatus() {
        return status;
    }

    @Override
    public void setDeviceStatus(DeviceStatus status) {
        this.status = status;
    }

    @Override
    public void setDeviceStatusNotifyServer(DeviceStatus status) throws InterruptedException, SessionException {
        if (isClosed()) {
            throw new SessionException("session is null or closed!");
        }

        LOGGER.info("notify server status message - deviceId: " + deviceId + ",current status " + this.status.name() + ",change status : " + status.name());

        SerializeType serializeType = Global.getInstance().getClientConfig().getSerializeType();
        //此处需要发送状态变更到客户端
        ctx.writeAndFlush(ProtocolHelper.createStatusMessage(MessageFromType.CLIENT, deviceId, status,serializeType)).sync();
        //修改设备状态
        this.status = status;
    }

    @Override
    public void close() {
        this.closed = true;
        this.status = DeviceStatus.DOWN;
        try {
            ctx.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            //从session工厂中移除
            SessionFactory.removeSession();
        }

    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (this.ctx != null && this.ctx.channel().isOpen()) {
                close();
            }
        } catch (Throwable t) {
            LOGGER.error("Device " + deviceId + " Session Release Error!:", t);
        } finally {
            super.finalize();
        }
    }

}
