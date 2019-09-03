package com.kevin.scepter.client.core.hotkey;


import com.kevin.message.protocol.message.IMessage;

/**
 * @author: kevin
 * @description: 返回消息处理
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:50
 */
public interface IReturnHandlerListener {

    public Object handler(IMessage message) throws Exception;

}
