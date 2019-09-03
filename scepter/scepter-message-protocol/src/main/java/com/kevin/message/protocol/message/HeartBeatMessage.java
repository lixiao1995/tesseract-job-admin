package com.kevin.message.protocol.message;

import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 心跳消息体
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:58
 */
public class HeartBeatMessage extends DefaultMessage {

    /**
     * 是否第一次请求,1是，0否
     */
    private int fstConn;

    public HeartBeatMessage() {

    }

    public HeartBeatMessage(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public MessageType messageType() {
        return MessageType.HeartBeat;
    }

    public int getFstConn() {
        return fstConn;
    }

    public void setFstConn(int fstConn) {
        this.fstConn = fstConn;
    }

}
