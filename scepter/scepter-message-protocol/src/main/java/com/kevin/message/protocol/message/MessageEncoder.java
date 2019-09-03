package com.kevin.message.protocol.message;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.ProtocolConst;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 数据加密
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:59
 */
public class MessageEncoder extends MessageToByteEncoder<Protocol> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Protocol p, ByteBuf out) throws Exception {
		out.writeBytes(ProtocolConst.P_START_TAG);
		out.writeBytes(p.toBytes());
		out.writeBytes(ProtocolConst.P_END_TAG);
	}
	
}
