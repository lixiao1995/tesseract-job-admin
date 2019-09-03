package com.kevin.scepter.client.core.server;

import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.utility.ProtocolHelper;
import com.kevin.scepter.client.core.context.Global;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author: kevin
 * @description: 客户端心跳处理类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:55
 */
public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                SerializeType serializeType = Global.getInstance().getClientConfig().getSerializeType();
                ctx.writeAndFlush(ProtocolHelper.createHeartBeatMessage(MessageFromType.CLIENT,
                        Global.getInstance().getClientConfig().getDeviceId(), false,serializeType));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
