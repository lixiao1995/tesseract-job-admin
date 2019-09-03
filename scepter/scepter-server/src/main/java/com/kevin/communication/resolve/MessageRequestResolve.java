package com.kevin.communication.resolve;

import com.kevin.message.protocol.message.RequestMessage;
import com.kevin.communication.core.context.BeatContext;

/**
 * @author kevin
 */
public class MessageRequestResolve implements IResolve<RequestMessage> {
	
	@Override
	public Class<RequestMessage> getResolveClass() {
		return RequestMessage.class;
	}

	@Override
	public RequestMessage resolve(BeatContext context , String paramName) {
		return context.getRequest();
	}

}
