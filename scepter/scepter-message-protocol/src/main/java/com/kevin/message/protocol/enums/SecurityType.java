package com.kevin.message.protocol.enums;


import com.kevin.message.protocol.security.ISecurity;
import com.kevin.message.protocol.security.UnSecurity;

/**
 * @author: kevin
 * @description: 加密类型枚举
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 17:50
 */
public enum SecurityType {

	/**
	 * 不加密
	 */
	UnSecurity(0 , new UnSecurity()),

	/**
	 * AES加/解密
	 */
	AES(1 , null),

	/**
	 * DES加/解密
	 */
	DES(2 , null);

	/**
	 * 加/解密编码
	 */
	private final int code;

	/**
	 * 加解密对应的对象实体
	 */
	private final ISecurity security;

	private SecurityType(int code , ISecurity security) {
		this.code = code;
		this.security = security;
	}

	public int getCode() {
		return code;
	}

	public ISecurity getSecurity() {
		return security;
	}

	public static SecurityType getSecurityType(int code) {
		for (SecurityType type : SecurityType.values()) {
			if (type.code == code) {
				return type;
			}
		}
		return null;
	}

}
