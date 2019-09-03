package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 数据溢出
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:52
 */
public class DataOverFlowException extends RemoteException {
	
	private static final long serialVersionUID = 38751782790059967L;

	public DataOverFlowException() {
		super(ExceptionType.DATA_OVER_FLOW_EXCEPTION);
	}

	public DataOverFlowException(String message) {
		super(ExceptionType.DATA_OVER_FLOW_EXCEPTION.getCode() , message);
	}

}
