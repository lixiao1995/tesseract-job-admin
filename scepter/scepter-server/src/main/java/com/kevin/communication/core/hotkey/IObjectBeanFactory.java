package com.kevin.communication.core.hotkey;

/**
 * @author: kevin
 * @description: 普通对象工厂
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:00
 */
public interface IObjectBeanFactory {

    /**
     * 注册指定的Class
     *
     * @param clazz - Class
     * @return Object
     * @throws Exception
     */
    public Object createBean(Class<?> clazz) throws Exception;

}
