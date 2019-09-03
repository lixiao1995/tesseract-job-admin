package com.kevin.communication.core.session;

import com.kevin.communication.core.context.Global;
import com.kevin.message.protocol.enums.DeviceStatus;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.utility.ProtocolHelper;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: session
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:03
 */
public class DefaultSession implements Session {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSession.class);

    /**
     * Channel通道
     */
    private final ChannelHandlerContext ctx;

    /**
     * 设备ID
     */
    private final String deviceId;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    /**
     * session是否关闭
     */
    private volatile boolean closed = false;

    public DefaultSession(ChannelHandlerContext ctx, String deviceId) {
        this.ctx = ctx;
        this.deviceId = deviceId;
        //默认空闲状态
        this.status = DeviceStatus.IDLE;
    }

    @Override
    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
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
    public void setDeviceStatusNotifyClient(DeviceStatus status) throws InterruptedException, SessionException {
        if (isClosed()) {
            throw new SessionException("deviceId:" + deviceId + " is closed!");
        }
        LOGGER.info("notify client status message -  - deviceId: " + this.deviceId + ",current status " + this.status.name() + ",change status : " + status.name());

        SerializeType serializeType = Global.getInstance().getServiceConfig().getSerializeType();
        //此处需要发送状态变更到客户端
        ctx.writeAndFlush(ProtocolHelper.createStatusMessage(MessageFromType.SERVER, this.deviceId, status,serializeType)).sync();
        //修改设备状态
        this.status = status;
    }

    @Override
    public void close() {
        closed = true;
        this.status = DeviceStatus.DOWN;
        try {
            ctx.close();
        } finally {
            SessionManager.getInstance().removeSession(this);
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

}
