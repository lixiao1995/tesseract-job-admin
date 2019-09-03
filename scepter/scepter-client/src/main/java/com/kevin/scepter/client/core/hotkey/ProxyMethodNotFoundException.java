package com.kevin.scepter.client.core.hotkey;

import java.lang.reflect.Method;

/**
 * @author: kevin
 * @description: 找不到代理方法异常
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:51
 */
public class ProxyMethodNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -632494869687017902L;

    public ProxyMethodNotFoundException(Class<?> proxyClass, Method method) {
        super("proxyClass:" + proxyClass.getName() + ",method:" + method.getName() + " not found by scan!");
    }

    public ProxyMethodNotFoundException(String message) {
        super(message);
    }

}
