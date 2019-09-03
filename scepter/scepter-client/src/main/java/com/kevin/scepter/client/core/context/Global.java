package com.kevin.scepter.client.core.context;

import com.google.inject.Injector;
import com.kevin.scepter.client.core.config.SocketClientConfig;

/**
 * @author: kevin
 * @description: 全局变量
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:48
 */
public final class Global {

    private static Global INSTANCE = new Global();

    public static Global getInstance() {
        return INSTANCE;
    }

    private Global() {

    }

    private Injector injector;

    private SocketClientConfig clientConfig;

    public SocketClientConfig getClientConfig() {
        return clientConfig;
    }

    public void setClientConfig(SocketClientConfig clientConfig) {
        if (this.clientConfig == null) {
            this.clientConfig = clientConfig;
        } else {
            throw new IllegalStateException("Cannot set configuration information repeatedly");
        }
    }

    public Injector getInjector() {
        return this.injector;
    }

    public <T> T resolve(Class<T> clazz) {
        return this.injector.getInstance(clazz);
    }

    public void setInjector(Injector injector) {
        if (this.injector == null) {
            this.injector = injector;
        } else {
            throw new IllegalStateException("Cannot set configuration information repeatedly");
        }
    }

}
