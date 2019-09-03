package com.kevin.scepter.springboot.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: kevin
 * @description: 服务端配置项目
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 14:52
 */
@ConfigurationProperties(prefix = "scepter.server")
public class ServerProperties {
	
	/**
	 * 读空闲时间设定，单位(秒)
	 */
	private int readerIdleTime = 20;
	
	/**
	 * 端口号
	 */
	private int port;
	
	/**
	 * 默认扫描的包路径
	 */
	private String basePackages;
	
	/**
	 * 是否打印慢方法
	 */
	private boolean slowMethod = false;
	
	/**
	 * 慢方法的耗时阈值，单位(毫秒)
	 */
	private long slowMethodMillis = 1000L;

	/**
	 * 序列化协议
	 */
	private Integer serializeType;

	public Integer getSerializeType() {
		return serializeType;
	}

	public void setSerializeType(Integer serializeType) {
		this.serializeType = serializeType;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBasePackages() {
		return basePackages;
	}

	public void setBasePackages(String basePackages) {
		this.basePackages = basePackages;
	}

	public int getReaderIdleTime() {
		return readerIdleTime;
	}

	public void setReaderIdleTime(int readerIdleTime) {
		this.readerIdleTime = readerIdleTime;
	}

	public long getSlowMethodMillis() {
		return slowMethodMillis;
	}

	public void setSlowMethodMillis(long slowMethodMillis) {
		this.slowMethodMillis = slowMethodMillis;
	}

	public boolean isSlowMethod() {
		return slowMethod;
	}

	public void setSlowMethod(boolean slowMethod) {
		this.slowMethod = slowMethod;
	}

}
