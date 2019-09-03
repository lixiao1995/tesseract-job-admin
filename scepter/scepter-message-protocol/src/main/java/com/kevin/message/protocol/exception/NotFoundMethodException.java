package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 没有找到要调用的方法
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:53
 */
public class NotFoundMethodException extends RemoteException {
	
	private static final long serialVersionUID = -4927016392684667768L;

	public NotFoundMethodException() {
		super(ExceptionType.NOT_FOUND_METHOD_EXCEPTION);
	}

	public NotFoundMethodException(String message) {
		super(ExceptionType.NOT_FOUND_METHOD_EXCEPTION.getCode() , message);
	}

}
