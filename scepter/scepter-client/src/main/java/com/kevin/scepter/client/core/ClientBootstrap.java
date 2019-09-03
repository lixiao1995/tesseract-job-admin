package com.kevin.scepter.client.core;

import com.google.inject.Guice;
import com.kevin.scepter.client.core.config.SocketClientConfig;
import com.kevin.scepter.client.core.context.CommunicationListener;
import com.kevin.scepter.client.core.context.Global;
import com.kevin.scepter.client.core.hotkey.GuiceModule;
import com.kevin.scepter.client.core.server.SocketServer;
import com.kevin.scepter.client.core.session.ISessionConnectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * @author: kevin
 * @description: 客户端启动类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:59
 */
public final class ClientBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientBootstrap.class);

    public ClientBootstrap(SocketClientConfig clientConfig) {
        //检查配置信息
        checkConfig(clientConfig);
        //设置到全局变量中
        Global.getInstance().setClientConfig(clientConfig);
    }

    /**
     * 添加Session连接监听器
     *
     * @param listener - ISessionConnectionListener
     */
    public void addSessionConnectionListener(ISessionConnectionListener listener) {
        CommunicationListener.addSessionConnectionListener(listener);
    }

    /**
     * 开启通信服务
     *
     * @throws InterruptedException
     */
    public void start() throws InterruptedException, UnknownHostException {
        LOGGER.info("----------------------start client netty server------------------");

        //加载guice注入服务
        loadGuice();

        //加载服务
        loadServer();

        LOGGER.info("----------------------start client netty server finish------------");
    }

    /**
     * 开启guice
     */
    private void loadGuice() {
        Global.getInstance().setInjector(Guice.createInjector(new GuiceModule()));
    }

    /**
     * 加载服务
     *
     * @throws InterruptedException
     */
    private void loadServer() throws InterruptedException, UnknownHostException {
        SocketServer server = new SocketServer();
        server.start();
    }

    /**
     * 检查配置
     *
     * @param clientConfig - SocketClientConfig
     */
    private void checkConfig(SocketClientConfig clientConfig) {
        if (clientConfig.getServerPort() <= 0) {
            throw new IllegalArgumentException("server port error");
        }
        if (clientConfig.getServerHost() == null || clientConfig.getServerHost().isEmpty()) {
            throw new IllegalArgumentException("server host can't be empty");
        }
        if (clientConfig.getDeviceId() == null || clientConfig.getDeviceId().isEmpty()) {
            throw new IllegalArgumentException("deviceId can't be empty");
        }
    }

}
