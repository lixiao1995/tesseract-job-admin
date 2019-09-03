package com.kevin.scepter.client.core.config;

import com.kevin.message.protocol.enums.SerializeType;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author: kevin
 * @description: 客户端配置
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:47
 */
public final class SocketClientConfig {

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * 服务地址
     */
    private String serverHost;


    /**
     * 服务端口
     */
    private int serverPort;

//    /**
//     * 客户端端口
//     */
//    private int clientPort;

    /**
     * 需要扫描的代理包路径
     */
    private String basePackages;

    /**
     * 客户端写空闲超时时间设定(秒)。具体作用就是如果write()方法超过writerIdleTime时间未被调用则会触发超时事件调用userEventTrigger()方法（向服务端发送心跳检测）
     */
    private int writerIdleTime = 5;

    /**
     * 客户端请求超时时间，秒
     */
    private long requestTimeout = 300;

    /**
     * 客户端异步超时时间，秒
     */
    private int asyncRequestTimeout = 20;

    /**
     * 服务器连接断开后的重试间隔，单位(秒)
     */
    private int reconnectTime = 5;

    /**
     * 心跳空闲超时时间，单位(秒)
     */
    private int idleHeartTimeout = 20;

    /**
     * 自定义心跳处理器
     */
    private ChannelInboundHandlerAdapter clientHeartBeatHandler;

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

    public ChannelInboundHandlerAdapter getClientHeartBeatHandler() {
        return clientHeartBeatHandler;
    }

    public void setClientHeartBeatHandler(ChannelInboundHandlerAdapter clientHeartBeatHandler) {
        this.clientHeartBeatHandler = clientHeartBeatHandler;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(String basePackages) {
        this.basePackages = basePackages;
    }

    public int getWriterIdleTime() {
        return writerIdleTime;
    }

    public void setWriterIdleTime(int writerIdleTime) {
        if (writerIdleTime <= 0) {
            throw new IllegalArgumentException("writerIdleTime Must be greater than zero!");
        }
        this.writerIdleTime = writerIdleTime;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(long requestTimeout) {
        if (requestTimeout <= 0) {
            throw new IllegalArgumentException("requestTimeout Must be greater than zero!");
        }
        this.requestTimeout = requestTimeout;
    }

    public int getReconnectTime() {
        return reconnectTime;
    }

    public void setReconnectTime(int reconnectTime) {
        if (reconnectTime <= 0) {
            throw new IllegalArgumentException("reconnectTime Must be greater than zero!");
        }
        this.reconnectTime = reconnectTime;
    }

    public int getIdleHeartTimeout() {
        return idleHeartTimeout;
    }

    public void setIdleHeartTimeout(int idleHeartTimeout) {
        if (idleHeartTimeout <= 0) {
            throw new IllegalArgumentException("idleHeartTimeout Must be greater than zero!");
        }
        this.idleHeartTimeout = idleHeartTimeout;
    }

    public int getAsyncRequestTimeout() {
        return asyncRequestTimeout;
    }

    public void setAsyncRequestTimeout(int asyncRequestTimeout) {
        if (asyncRequestTimeout <= 0) {
            throw new IllegalArgumentException("asyncRequestTimeout Must be greater than zero!");
        }
        this.asyncRequestTimeout = asyncRequestTimeout;
    }

}
