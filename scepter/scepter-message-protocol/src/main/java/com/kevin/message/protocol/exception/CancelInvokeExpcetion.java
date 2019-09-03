package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 取消调用
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:51
 */
public class CancelInvokeExpcetion extends RemoteException {
	
	private static final long serialVersionUID = 4400770616566227195L;

	public CancelInvokeExpcetion() {
		super(ExceptionType.REBOOT_EXCEPTION);
	}

	public CancelInvokeExpcetion(String message) {
		super(ExceptionType.REBOOT_EXCEPTION.getCode() , message);
	}

}
