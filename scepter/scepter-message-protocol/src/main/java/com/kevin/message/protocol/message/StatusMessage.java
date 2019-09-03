package com.kevin.message.protocol.message;

import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 状态消息体
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:00
 */
public class StatusMessage extends DefaultMessage {
	
	/**
	 * 状态编号
	 */
	private int status;
	
	public StatusMessage() {
		
	}
	
	public StatusMessage(String deviceId , int status) {
		this.deviceId = deviceId;
		this.status = status;
	}

	@Override
	public MessageType messageType() {
		return MessageType.Status;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
