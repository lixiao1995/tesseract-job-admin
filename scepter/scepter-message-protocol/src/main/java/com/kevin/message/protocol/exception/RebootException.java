package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 服务重启
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:55
 */
public class RebootException extends RemoteException {
	
	private static final long serialVersionUID = -4327450650738132374L;

	public RebootException() {
		super(ExceptionType.REBOOT_EXCEPTION);
	}

	public RebootException(String message) {
		super(ExceptionType.REBOOT_EXCEPTION.getCode() , message);
	}

}
