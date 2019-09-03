package com.kevin.communication.core.hotkey;

/**
 * @author: kevin
 * @description: 默认的代理工厂类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:59
 */
public class DefaultProxyFactory extends AbstractProxyFactory {

    private IObjectBeanFactory beanFactory = new DefaultObjectBeanFactory();

    @Override
    public IObjectBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

}
