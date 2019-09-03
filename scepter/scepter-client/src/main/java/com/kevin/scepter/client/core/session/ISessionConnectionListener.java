package com.kevin.scepter.client.core.session;

/**
 * @author: kevin
 * @description: Session连接监听器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:57
 */
public interface ISessionConnectionListener {

    /**
     * Session连接后调用的事件通知
     *
     * @param event - SessionConnectionEvent
     * @return boolean - 如果不执行下一个监听器，则return false
     */
    public boolean onConnection(SessionConnectionEvent event);

}
