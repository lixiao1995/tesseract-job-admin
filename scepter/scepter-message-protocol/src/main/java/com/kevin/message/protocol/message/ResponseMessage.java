package com.kevin.message.protocol.message;


import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 数据响应体
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:00
 */
public class ResponseMessage extends DefaultMessage {
	
	private String body;
	
	/**
	 * 终端时间/服务器时间
	 */
	private long terminalTime;
	
	public ResponseMessage() {
		
	}
	
	public ResponseMessage(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public MessageType messageType() {
		return MessageType.Response;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getTerminalTime() {
		return terminalTime;
	}

	public void setTerminalTime(long terminalTime) {
		this.terminalTime = terminalTime;
	}

}
