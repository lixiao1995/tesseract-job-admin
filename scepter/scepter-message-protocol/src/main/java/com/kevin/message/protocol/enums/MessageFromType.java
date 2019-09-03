package com.kevin.message.protocol.enums;

/**
 * @author: kevin
 * @description: 消息来源枚举
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:46
 */
public enum MessageFromType {

	/**
	 * 未知
	 */
	UNKNOW(0),
	/**
	 * 服务端
	 */
	SERVER(1),
	/**
	 * 客户端
	 */
	CLIENT(2)
	;
	
	/**
	 * 编码
	 */
	private final int code;
	
	MessageFromType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	/**
	 * 根据消息来源编码获取枚举
	 * @param code - int
	 * @return MessageFromType
	 * @throws Exception
	 */
	public static MessageFromType getMessageFromType(int code) throws Exception {
		for (MessageFromType type : MessageFromType.values()) {
			if (type.code == code) {
				return type;
			}
		}
		return MessageFromType.UNKNOW;
	}

}
