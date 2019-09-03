package com.kevin.scepter.client.core.server;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.enums.MessageType;
import com.kevin.message.protocol.message.StatusMessage;
import com.kevin.scepter.client.core.message.IMessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CorruptedFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 客户端消息处理类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:55
 */
public class ClientMessageHandler extends SimpleChannelInboundHandler<Protocol> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMessageHandler.class);

    private IMessageProcessor messageProcessor;

    public ClientMessageHandler(IMessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol p) throws Exception {
        MessageType mt = p.getMessageType();
        switch (mt) {
            case Request:
                //服务端直接push的数据
                messageProcessor.processReceiveRequestMessage(ctx, p);
                break;
            case Response:
                //TODO 此处是否需要检查合法性？？
                //客户端发送，服务端响应的数据
                messageProcessor.processReceiveResponseMessage(ctx, p);
                break;
            case Exception:
                //输出异常
                messageProcessor.processReceiveExceptionMessage(ctx, p);
                break;
            case Status:
                messageProcessor.processReceiveStatusMessage(ctx, (StatusMessage) p.getEntity());
                break;
            default:
                LOGGER.warn("have not match messageType");
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof CorruptedFrameException) {
            // something goes bad with decoding
            LOGGER.warn("Error decoding a packet, probably a bad formatted packet, message: " + cause.getMessage());
        } else {
            LOGGER.error("Ugly error on networking", cause);
        }
    }

}
