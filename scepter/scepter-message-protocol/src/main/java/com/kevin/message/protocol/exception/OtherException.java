package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 其它错误
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:54
 */
public class OtherException extends RemoteException {
	
	private static final long serialVersionUID = 618798273496916633L;

	public OtherException() {
		super(ExceptionType.OTHER_EXCEPTION);
	}

	public OtherException(String message) {
		super(ExceptionType.OTHER_EXCEPTION.getCode() , message);
	}

}
