package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 服务调用异常
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:56
 */
public class ServiceInvokeException extends RemoteException {

	private static final long serialVersionUID = -2710640874887323375L;

	public ServiceInvokeException() {
		super(ExceptionType.SERVICE_INVOKE_EXCEPTION);
	}

	public ServiceInvokeException(String message) {
		super(ExceptionType.SERVICE_INVOKE_EXCEPTION.getCode() , message);
	}

}
