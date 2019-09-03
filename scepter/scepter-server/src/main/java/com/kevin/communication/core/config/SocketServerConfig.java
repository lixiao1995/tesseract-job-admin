package com.kevin.communication.core.config;

import com.kevin.communication.core.hotkey.IProxyFactory;
import com.kevin.communication.core.server.IMessageProcessor;
import com.kevin.communication.core.session.SocketMessageProcessor;
import com.kevin.message.protocol.enums.SerializeType;

/**
 * @author: kevin
 * @description: 服务器端配置参数
 * @updateRemark: 更新项目
 * @date: 2019-07-29 18:50
 */
public final class SocketServerConfig {
	
	/**
	 * 读空闲超时时间设定(秒)，如果channelRead()方法超过readerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法；
	 */
	private int readerIdleTime = 20;
	
	/**
	 * 默认开启端口
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
	 * 默认的代理类工厂
	 */
	private IProxyFactory proxyFactory;

	/**
	 * 消息处理类
	 */
	private SocketMessageProcessor messageProcessor;

	/**
	 * 序列化方式，目前只支持JSON,具体数据，查看以下枚举类,如果不填默认JSON
	 * {@Link com.kevin.message.protocol.enums.SerializeType}
	 */
	private SerializeType serializeType;

	public SerializeType getSerializeType() {
		if (serializeType == null) {
			serializeType = SerializeType.JSON;
		}
		return serializeType;
	}

	public void setSerializeType(SerializeType serializeType) {
		this.serializeType = serializeType;
	}

	public SocketMessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	public void setMessageProcessor(SocketMessageProcessor messageProcessor) {
		this.messageProcessor = messageProcessor;
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

	public IProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public void setProxyFactory(IProxyFactory proxyFactory) {
		this.proxyFactory = proxyFactory;
	}

	public int getReaderIdleTime() {
		return readerIdleTime;
	}

	public void setReaderIdleTime(int readerIdleTime) {
		if(readerIdleTime <= 0) {
			throw new IllegalArgumentException("readerIdleTime Must be greater than zero!");
		}
		this.readerIdleTime = readerIdleTime;
	}

	public long getSlowMethodMillis() {
		return slowMethodMillis;
	}

	public void setSlowMethodMillis(long slowMethodMillis) {
		if(slowMethodMillis < 0) {
			throw new IllegalArgumentException("slowMethodMillis Must be greater than zero!");
		}
		this.slowMethodMillis = slowMethodMillis;
	}

	public boolean isSlowMethod() {
		return slowMethod;
	}

	public void setSlowMethod(boolean slowMethod) {
		this.slowMethod = slowMethod;
	}

}
