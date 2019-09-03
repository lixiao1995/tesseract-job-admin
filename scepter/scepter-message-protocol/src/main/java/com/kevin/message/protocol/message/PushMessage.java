package com.kevin.message.protocol.message;

import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 服务端push消息
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-07 11:05
 */
public class PushMessage  extends DefaultMessage {
    private String mapping;

    private String body;

    public PushMessage() {

    }

    public PushMessage(String deviceId , String mapping) {
        this.deviceId = deviceId;
        this.mapping = mapping;
    }

    @Override
    public MessageType messageType() {
        return MessageType.Push;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
