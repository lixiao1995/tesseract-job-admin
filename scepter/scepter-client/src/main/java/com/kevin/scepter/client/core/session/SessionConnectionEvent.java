package com.kevin.scepter.client.core.session;


import com.kevin.message.protocol.message.IMessage;

/**
 * @author: kevin
 * @description: Session连接事件对象
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:57
 */
public final class SessionConnectionEvent {

    /**
     * 心跳消息
     */
    private IMessage message;

    /**
     * Session
     */
    private ISession session;

    public SessionConnectionEvent(IMessage message, ISession session) {
        this.message = message;
        this.session = session;
    }

    public IMessage getMessage() {
        return message;
    }

    public void setMessage(IMessage message) {
        this.message = message;
    }

    public ISession getSession() {
        return session;
    }

    public void setSession(ISession session) {
        this.session = session;
    }

}
