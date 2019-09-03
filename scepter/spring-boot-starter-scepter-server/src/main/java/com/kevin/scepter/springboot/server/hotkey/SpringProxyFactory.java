package com.kevin.scepter.springboot.server.hotkey;

import com.kevin.communication.core.hotkey.AbstractProxyFactory;
import com.kevin.communication.core.hotkey.IObjectBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author: kevin
 * @description: spring代理对象工厂
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 14:53
 */
public class SpringProxyFactory extends AbstractProxyFactory {
	
	private IObjectBeanFactory beanFactory;
	
	public SpringProxyFactory(ApplicationContext context) {
		this.beanFactory = new SpringObjectBeanFactory(context);
	}

	@Override
	public IObjectBeanFactory getBeanFactory() {
		return beanFactory;
	}

}
