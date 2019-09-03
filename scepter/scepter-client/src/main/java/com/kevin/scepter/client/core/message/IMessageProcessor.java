package com.kevin.scepter.client.core.message;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.message.StatusMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: kevin
 * @description: 消息处理接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:53
 */
public interface IMessageProcessor {

    /**
     * 接收到请求消息
     *
     * @param ctx - ChannelHandlerContext
     * @param p   - 消息体
     */
    void processReceiveRequestMessage(ChannelHandlerContext ctx, Protocol p);

    /**
     * 接收到响应消息
     *
     * @param ctx - ChannelHandlerContext
     * @param p   - 消息体
     */
    void processReceiveResponseMessage(ChannelHandlerContext ctx, Protocol p);

    /**
     * 接收到异常消息
     *
     * @param ctx - ChannelHandlerContext
     * @param p   - 消息体
     */
    void processReceiveExceptionMessage(ChannelHandlerContext ctx, Protocol p);

    /**
     * 接受到状态变更消息
     *
     * @param ctx     - ChannelHandlerContext
     * @param message - 消息体
     */
    void processReceiveStatusMessage(ChannelHandlerContext ctx, StatusMessage message);

}
