package com.kevin.communication.core.server;

import com.kevin.communication.core.context.ServerType;

/**
 * @author: kevin
 * @description: 服务器接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:03
 */
public interface IServer {

    /**
     * 开启服务器
     *
     * @throws Exception
     */
    public abstract void start() throws Exception;

    /**
     * 关闭服务器
     *
     * @throws Exception
     */
    public abstract void stop() throws Exception;

    /**
     * 服务类型
     *
     * @return ServerType
     */
    public abstract ServerType getServerType();

}
