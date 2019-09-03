package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 客户端调用超时异常
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:56
 */
public class TimeoutException extends RemoteException {

	private static final long serialVersionUID = 4345935963121140739L;

	public TimeoutException() {
		super(ExceptionType.TIME_OUT);
	}

	public TimeoutException(String message) {
		super(ExceptionType.TIME_OUT.getCode() , message);
	}

}
