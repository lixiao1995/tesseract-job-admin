package com.kevin.message.protocol.enums;

/**
 * @author: kevin
 * @description: 平台类型枚举
 * @updateRemark: 更新项目
 * @date: 2019-07-29 17:49
 */
public enum PlatformType {

	/**
	 * Java客户端
	 */
	Java(0),
	/**
	 * C客户端
	 */
	C(1);

	/**
	 * 平台类型编码
	 */
	private final int code;
	
	private PlatformType(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static PlatformType getPlatformType(int code) {
		for (PlatformType type : PlatformType.values()) {
			if (type.code == code) {
				return type;
			}
		}
		return null;
	}

}
