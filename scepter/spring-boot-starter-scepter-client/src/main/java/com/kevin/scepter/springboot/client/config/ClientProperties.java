package com.kevin.scepter.springboot.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: kevin
 * @description: 客户端配置项目
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 15:08
 */
@ConfigurationProperties(prefix = "socket.client")
public class ClientProperties {

    /**
     * 客户端ID
     */
    private String deviceId;
    /**
     * 默认扫描的包路径
     */
    private String basePackages;
    /**
     * 要链接的服务端host
     */
    private String serverHost;
    /**
     * 要链接的服务端端口
     */
    private Integer serverPort;
    /**
     * 请求超时时间
     */
    private Integer requestTimeout;
    /**
     * 客户端写空闲超时时间设定(秒)。具体作用就是如果write()方法超过writerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法（向服务端发送心跳检测）
     */
    private Integer writerIdleTime;

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Integer getWriterIdleTime() {
        return writerIdleTime;
    }

    public void setWriterIdleTime(Integer writerIdleTime) {
        this.writerIdleTime = writerIdleTime;
    }

}
