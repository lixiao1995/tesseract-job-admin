package com.kevin.communication.core.session;

import com.kevin.message.protocol.enums.DeviceStatus;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: kevin
 * @description: session
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:04
 */
public interface Session {

    /**
     * 获得连接通道
     *
     * @return ChannelHandlerContext
     */
    public ChannelHandlerContext getCtx();

    /**
     * 获得设备ID
     *
     * @return String
     */
    public String getDeviceId();

    /**
     * 获得当前设备状态
     *
     * @return DeviceStatus
     */
    public DeviceStatus getDeviceStatus();

    /**
     * 修改当前设备的状态
     *
     * @param status - DeviceStatus
     */
    public void setDeviceStatus(DeviceStatus status);

    /**
     * 修改当前设备的状态并且通知客户端
     *
     * @param status - DeviceStatus
     * @throws InterruptedException
     * @throws SessionException
     */
    public void setDeviceStatusNotifyClient(DeviceStatus status) throws InterruptedException, SessionException;

    /**
     * 关闭session
     */
    public void close();

    /**
     * session是否关闭
     *
     * @return boolean
     */
    public boolean isClosed();

}
