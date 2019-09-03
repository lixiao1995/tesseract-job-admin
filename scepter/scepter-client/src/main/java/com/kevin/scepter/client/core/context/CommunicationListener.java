package com.kevin.scepter.client.core.context;


import com.kevin.scepter.client.core.session.ISessionConnectionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: kevin
 * @description: 通信监听器工具类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:47
 */
public final class CommunicationListener {

    /**
     * Session连接监听器
     */
    private static final List<ISessionConnectionListener> SESSION_CONNECTION_LISTENER_LIST = new ArrayList<>();

    /**
     * 添加一个Session连接监听器
     *
     * @param listener - ISessionConnectionListener
     */
    public static void addSessionConnectionListener(ISessionConnectionListener listener) {
        SESSION_CONNECTION_LISTENER_LIST.add(listener);
    }

    /**
     * 移除一个Session连接监听器
     *
     * @param listener - ISessionConnectionListener
     */
    public static void removeSessionConnectionListener(ISessionConnectionListener listener) {
        SESSION_CONNECTION_LISTENER_LIST.remove(listener);
    }

    /**
     * 清空Session连接监听器
     */
    public static void clearSessionConnectionListener() {
        SESSION_CONNECTION_LISTENER_LIST.clear();
    }

    /**
     * 获取所有的Session连接监听器
     *
     * @return List<ISessionConnectionListener>
     */
    public static List<ISessionConnectionListener> getSessionConnectionListener() {
        return SESSION_CONNECTION_LISTENER_LIST;
    }

}
