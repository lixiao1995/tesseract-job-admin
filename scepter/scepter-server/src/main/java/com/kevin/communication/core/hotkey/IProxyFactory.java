package com.kevin.communication.core.hotkey;

import java.util.Map;

/**
 * @author: kevin
 * @description: 代理对象工厂
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:00
 */
public interface IProxyFactory {

    /**
     * 初始化命令
     *
     * @param commandMap - Map<String, CommandInfo>
     */
    public void init(Map<String, CommandInfo> commandMap);

    /**
     * 获得一个代理
     *
     * @param mapping - 获得一个指定的代理
     * @return IProxyStub
     */
    public IProxyStub getProxy(String mapping);

}
