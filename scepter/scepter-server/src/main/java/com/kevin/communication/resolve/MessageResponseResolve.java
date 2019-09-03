package com.kevin.communication.resolve;

import com.kevin.message.protocol.message.ResponseMessage;
import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class MessageResponseResolve implements IResolve<ResponseMessage> {
	
	@Override
	public Class<ResponseMessage> getResolveClass() {
		return ResponseMessage.class;
	}

	@Override
	public ResponseMessage resolve(BeatContext context , String paramName) {
		return context.getResponse();
	}

}
