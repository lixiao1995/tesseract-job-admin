package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 协议错误
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:54
 */
public class ProtocolException extends RemoteException {
	
	private static final long serialVersionUID = -2629327321768097883L;

	public ProtocolException() {
		super(ExceptionType.PROTOCOL);
	}

	public ProtocolException(String message) {
		super(ExceptionType.PROTOCOL.getCode() , message);
	}

}
