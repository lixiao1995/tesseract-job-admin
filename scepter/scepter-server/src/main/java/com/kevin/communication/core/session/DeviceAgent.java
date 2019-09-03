package com.kevin.communication.core.session;

import com.kevin.communication.utils.CreateSessionKey;
import com.kevin.message.protocol.enums.DeviceStatus;
import io.netty.channel.ChannelHandlerContext;

import java.net.SocketAddress;

/**
 * @author: kevin
 * @description: 设备信息对外接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:04
 */
public final class DeviceAgent {

    /**
     * 判断设备是否已连接
     *
     * @param ctx - 设备地址
     * @return boolean
     */
    public static final boolean isConnection(ChannelHandlerContext ctx) {
        String key = CreateSessionKey.inetSocketAddressByChannelHandlerContexts(ctx);
        if (key != null) {
            return SessionManager.getInstance().getSession(key) != null;
        }
        return false;
    }

    /**
     * 获得当前设备的状态信息
     *
     * @param ctx - 设备地址
     * @return DeviceStatus
     */
    public static final DeviceStatus getDeviceStatus(ChannelHandlerContext ctx) {
        String key = CreateSessionKey.inetSocketAddressByChannelHandlerContexts(ctx);
        Session session = SessionManager.getInstance().getSession(key);
        if (session == null) {
            return DeviceStatus.UNKNOWN;
        }

        return session.getDeviceStatus();
    }

    /**
     * 修改设备的当前状态
     *
     * @param ctx    - 设备地址
     * @param status - DeviceStatus
     * @throws InterruptedException
     * @throws SessionException
     */
    public static final void changeDeviceStatus(ChannelHandlerContext ctx, DeviceStatus status) throws
            InterruptedException, SessionException {
        String key = CreateSessionKey.inetSocketAddressByChannelHandlerContexts(ctx);
        Session session = SessionManager.getInstance().getSession(key);
        if (session == null) {
            throw new SessionException("current client session is null");
        }
        //修改设备状态
        session.setDeviceStatusNotifyClient(status);
    }

    private DeviceAgent() {

    }

}
