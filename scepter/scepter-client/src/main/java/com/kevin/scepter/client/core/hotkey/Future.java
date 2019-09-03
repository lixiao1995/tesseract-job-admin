package com.kevin.scepter.client.core.hotkey;

import com.kevin.message.protocol.exception.RemoteException;
import com.kevin.message.protocol.message.IMessage;

import java.util.concurrent.TimeUnit;

/**
 * @author: kevin
 * @description: Future Interface
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:49
 */
public interface Future {

    /**
     * 取消任务
     *
     * @return boolean
     */
    boolean cancel();

    /**
     * 判断任务是否被取消
     *
     * @return boolean
     */
    boolean isCancelled();

    /**
     * 阻塞获得结果
     *
     * @return result.
     */
    String get() throws InterruptedException, RemoteException;

    /**
     * 阻塞获得结果
     *
     * @param clazz - 要转换的类Class
     * @return result.
     */
    <T> T get(Class<T> clazz) throws InterruptedException, RemoteException;

    /**
     * 阻塞获得结果并设置超时时间，最大阻塞时间不能超过 {@link com.kevin.scepter.client.core.config.SocketClientConfig#asyncRequestTimeout}
     *
     * @param timeout - 超时时间
     * @param unit    - 超时单位
     * @return result.
     */
    String get(int timeout, TimeUnit unit) throws InterruptedException, RemoteException;

    /**
     * 阻塞获得结果并设置超时时间，最大阻塞时间不能超过 {@link com.kevin.scepter.client.core.config.SocketClientConfig#asyncRequestTimeout}
     *
     * @param timeout - 超时时间
     * @param unit    - 超时单位
     * @param clazz   - 要转换的类Class
     * @return result.
     */
    <T> T get(int timeout, TimeUnit unit, Class<T> clazz) throws InterruptedException, RemoteException;

    /**
     * 接收返回数据
     *
     * @param message - IMessage
     */
    public void received(IMessage message);

    /**
     * 检查任务是否已完成
     *
     * @return done or not.
     */
    boolean isDone();

}
