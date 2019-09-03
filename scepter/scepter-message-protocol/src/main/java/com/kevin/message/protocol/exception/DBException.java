package com.kevin.message.protocol.exception;

/**
 * @author: kevin
 * @description: 数据库错误
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:52
 */
public class DBException extends RemoteException {

	private static final long serialVersionUID = -5755953381689748732L;

	public DBException() {
		super(ExceptionType.DB);
	}

	public DBException(String message) {
		super(ExceptionType.DB.getCode() , message);
	}

}
