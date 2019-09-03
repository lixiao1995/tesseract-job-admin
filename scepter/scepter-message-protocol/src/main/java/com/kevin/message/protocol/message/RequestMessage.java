package com.kevin.message.protocol.message;

import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 数据请求体
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:59
 */
public class RequestMessage extends DefaultMessage {
	
	private String mapping;
	
	private String body;
	
	public RequestMessage() {
		
	}
	
	public RequestMessage(String deviceId , String mapping) {
		this.deviceId = deviceId;
		this.mapping = mapping;
	}
	
	@Override
	public MessageType messageType() {
		return MessageType.Request;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
