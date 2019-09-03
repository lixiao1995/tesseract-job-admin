package com.kevin.scepter.client.core.hotkey;

/**
 * @author: kevin
 * @description: 超出队列容量异常
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:49
 */
public class ExceedCapacityException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 5764367750778749657L;

    public ExceedCapacityException(String message) {
        super(message);
    }

    public ExceedCapacityException(String message, Throwable t) {
        super(message, t);
    }

}
