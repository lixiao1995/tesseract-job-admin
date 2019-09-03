package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 参数传递出错
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:54
 */
public class ParaException extends RemoteException {

	private static final long serialVersionUID = 6245468023216635789L;

	public ParaException() {
		super(ExceptionType.PARA_EXCEPTION);
	}

	public ParaException(String message) {
		super(ExceptionType.PARA_EXCEPTION.getCode() , message);
	}

}
