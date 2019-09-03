package com.kevin.springboot.scepter.client.test.command.impl;

import com.google.inject.Inject;
import com.kevin.scepter.client.core.hotkey.ProxyModule;
import com.kevin.springboot.scepter.client.test.command.IServiceRemote;
import com.kevin.springboot.scepter.client.test.command.ITestCommand;

@ProxyModule(from= ITestCommand.class)
public class TestCommandImpl implements ITestCommand {
	
	@Inject
	private IServiceRemote serviceRemote;
	@Override
	public void sayHello() {
		System.out.println("sayHello返回的数据"+serviceRemote.sayHello("abc" , 101));
	}

	@Override
	public String getTask(String json) {
		return serviceRemote.getTask(json);
	}

}
