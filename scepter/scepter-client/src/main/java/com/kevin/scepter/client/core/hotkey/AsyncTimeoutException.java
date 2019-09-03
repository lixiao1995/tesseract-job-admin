package com.kevin.scepter.client.core.hotkey;

/**
 * @author: kevin
 * @description: 异步消息执行超时
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:48
 */
public class AsyncTimeoutException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 5764367750778749657L;

    public AsyncTimeoutException(String message) {
        super(message);
    }

    public AsyncTimeoutException(String message, Throwable t) {
        super(message, t);
    }

}
