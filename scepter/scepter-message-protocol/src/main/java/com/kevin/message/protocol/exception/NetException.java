package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 网络连接异常
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:53
 */
public class NetException extends RemoteException {
	
	private static final long serialVersionUID = -2981860571118797570L;

	public NetException() {
		super(ExceptionType.NET);
	}

	public NetException(String message) {
		super(ExceptionType.NET.getCode() , message);
	}

}
