package com.kevin.message.protocol.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author: kevin
 * @description: 序列化出错
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:53
 */
public class SerializeException extends RemoteException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6585159057755382478L;

	public SerializeException(JsonProcessingException e) {
		super(ExceptionType.SERIALIZE_EXCEPTION);
	}

	public SerializeException(String message) {
		super(ExceptionType.SERIALIZE_EXCEPTION.getCode() , message);
	}

}
