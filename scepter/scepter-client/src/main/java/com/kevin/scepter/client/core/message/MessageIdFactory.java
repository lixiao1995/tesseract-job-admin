package com.kevin.scepter.client.core.message;

import com.kevin.scepter.client.core.config.ClientConstant;

/**
 * @author: kevin
 * @description: 消息id工厂
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:53
 */
public final class MessageIdFactory {

    private static int SESSION_ID = 1;

    /**
     * 创建MessageId
     *
     * @return int
     */
    public static int createMessageId() {
        synchronized (MessageIdFactory.class) {
            if (SESSION_ID > ClientConstant.MAX_MESSAGE_ID) {
                SESSION_ID = 1;
            }
            return SESSION_ID++;
        }
    }

}
