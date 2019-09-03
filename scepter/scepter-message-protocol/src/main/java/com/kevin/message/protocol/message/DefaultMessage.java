package com.kevin.message.protocol.message;

import java.io.Serializable;

/**
 * @author: kevin
 * @description: 抽象消息体
 * @updateRemark: 更新日志
 * @date: 2019-07-29 17:57
 */
public abstract class DefaultMessage implements IMessage, Serializable {
	
	protected String deviceId;
	
	protected long messageTime;

	private String ip;

	private String port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	@Override
	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

}
