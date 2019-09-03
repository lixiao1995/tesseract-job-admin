package com.kevin.message.protocol.message;

import com.kevin.message.protocol.enums.MessageType;

/**
 * @author: kevin
 * @description: 数据获取接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:58
 */
public interface IMessage {
	
	/**
	 * 消息的时间
	 */
	public long getMessageTime();
	
	/**
	 * 获得设备ID
	 * @return String
	 */
	public String getDeviceId();
	
	/**
	 * 消息类型
	 * @return MessageType
	 */
	public MessageType messageType();

}
