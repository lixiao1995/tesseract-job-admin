package com.kevin.message.protocol.enums;

import com.kevin.message.protocol.message.ExceptionMessage;
import com.kevin.message.protocol.message.HeartBeatMessage;
import com.kevin.message.protocol.message.PushMessage;
import com.kevin.message.protocol.message.RequestMessage;
import com.kevin.message.protocol.message.ResponseMessage;
import com.kevin.message.protocol.message.StatusMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: kevin
 * @description: 消息描述协议类型
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:47
 */
public enum MessageType {

	/**
	 * 响应
	 */
	Response(1 , ResponseMessage.class),
	/**
	 * 请求
	 */
	Request(2 , RequestMessage.class),
	/**
	 * 状态
	 */
	Status(3 , StatusMessage.class),
	/**
	 *	心跳
	 */
	HeartBeat(8 , HeartBeatMessage.class),
	/**
	 *	异常
	 */
	Exception(9 , ExceptionMessage.class),
	/**
	 * 服务端push消息
	 */
	Push(10 , PushMessage.class);

	private static final Map<Integer , MessageType> CODE_TYPE_MAP = new HashMap<>();
	private static final Map<Class<?> , MessageType> MESSAGE_CLASS_TYPE_MAP = new HashMap<>();
	private static final Map<Integer , Class<?>> TYPE_MESSAGE_CLASS_MAP = new HashMap<>();

	static {
		for(MessageType t : MessageType.values()) {
			CODE_TYPE_MAP.put(t.code, t);
			MESSAGE_CLASS_TYPE_MAP.put(t.messageType, t);
			TYPE_MESSAGE_CLASS_MAP.put(t.code, t.messageType);
		}
	}

	/**
	 * 消息类型编码
	 */
	private final int code;

	/**
	 * 消息对应的Class类
	 */
	private final Class<?> messageType;

	private MessageType(int code , Class<?> messageType) {
		this.code = code;
		this.messageType = messageType;
	}

	public int getCode() {
		return code;
	}

	public Class<?> getMessageType() {
		return messageType;
	}

	/**
	 * 根据会话协议类型码获取枚举
	 * @param code - int
	 * @return MessageType
	 * @throws java.lang.Exception
	 */
	public static MessageType getMessageType(int code) throws java.lang.Exception {
		MessageType type = CODE_TYPE_MAP.get(code);
		if(type == null) {
			throw new Exception("末知的MessageType:" + code);
		}

		return type;
	}

	/**
	 * 根据消息Class类型获取枚举
	 * @param clazz - Class
	 * @return MessageType
	 * @throws java.lang.Exception
	 */
	public static MessageType getMessageType(Class<?> clazz) {
		MessageType type = MESSAGE_CLASS_TYPE_MAP.get(clazz);

		if(type == null) {
			type = MessageType.Exception;
		}

		return type;
	}

	/**
	 * 根据消息类型编码枚举来获取对应协议对象
	 * @param code - 消息类型编码
	 * @return Class
	 * @throws java.lang.Exception
	 */
	public static Class<?> getMessageClass(int code) throws java.lang.Exception {
		Class<?> clazz = TYPE_MESSAGE_CLASS_MAP.get(code);
		if(clazz == null) {
			throw new Exception("末知的MessageClass:" + code);
		}

		return clazz;
	}

	/**
	 * 根据Protocol对象获取对应的协议枚举
	 * @param obj - Object
	 * @return MessageType
	 * @throws java.lang.Exception
	 */
	public static MessageType getMessageType(Object obj) {
		return getMessageType(obj.getClass());
	}

}
