package com.kevin.communication.core.server;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.message.HeartBeatMessage;
import com.kevin.message.protocol.message.StatusMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: kevin
 * @description: 消息处理接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:03
 */
public interface IMessageProcessor {

    /**
     * 接收到心跳消息的处理
     *
     * @param ctx     - ChannelHandlerContext
     * @param message - 消息协议体
     */
    void processReceiveHeartMessage(ChannelHandlerContext ctx, HeartBeatMessage message);

    /**
     * 断开连接请求
     *
     * @param ctx - ChannelHandlerContext
     */
    void processReceiveDisconnect(ChannelHandlerContext ctx);

    /**
     * 接收消息
     *
     * @param ctx - ChannelHandlerContext
     * @param msg - 消息协议体
     */
    void processReceiveMessage(ChannelHandlerContext ctx, Protocol msg);

    /**
     * 接收状态消息
     *
     * @param ctx     - ChannelHandlerContext
     * @param message - 状态消息对象
     */
    void processStatusMessage(ChannelHandlerContext ctx, StatusMessage message);

}
