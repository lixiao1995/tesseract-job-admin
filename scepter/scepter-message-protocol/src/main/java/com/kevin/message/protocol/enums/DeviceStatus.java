package com.kevin.message.protocol.enums;

/**
 * @author: kevin
 * @description: 设备状态枚举
 * @updateRemark: 更新项目
 * @date: 2019-07-29 17:45
 */
public enum DeviceStatus {

	/**
	 * 未知
	 */
	UNKNOWN(0),
	/**
	 * 空闲
	 */
	IDLE(1),
	/**
	 *忙碌
	 */
	BUSY(2),
	/**
	 * 下线
	 */
	DOWN(9)
	;
	
	private final int code;
	
	DeviceStatus(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
	public static DeviceStatus getDeviceState(int code) {
		for (DeviceStatus s : DeviceStatus.values()) {
			if (s.code == code) {
				return s;
			}
		}
		return UNKNOWN;
	}

}
