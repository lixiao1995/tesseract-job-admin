package com.kevin.scepter.server.test;

import com.kevin.communication.core.NettyBootstrap;
import com.kevin.communication.core.config.SocketServerConfig;
import com.kevin.communication.core.context.Global;
import com.kevin.communication.core.hotkey.DefaultProxyFactory;
import com.kevin.communication.core.session.Session;
import com.kevin.communication.core.session.SessionManager;
import com.kevin.communication.core.session.SocketMessageProcessor;
import com.kevin.message.protocol.enums.SerializeType;

import java.util.Map;

/**
 * @author: kevin
 * @description: 测试类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-21 11:35
 */
public class ServerTest {

	public static void main(String[] args) throws InterruptedException {
		//初始化配置信息
		SocketServerConfig serviceConfig = new SocketServerConfig();
		serviceConfig.setBasePackages("com.kevin.scepter.server.test.server.command");
		serviceConfig.setPort(9527);
		serviceConfig.setProxyFactory(new DefaultProxyFactory());
		serviceConfig.setMessageProcessor(new SocketMessageProcessor());
		serviceConfig.setSerializeType(SerializeType.HESSIAN2);
		NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);
		boostrap.start();
		Map<String, Session> sessionMap = SessionManager.getInstance().getSessionMap();
		sessionMap.entrySet().stream().forEach(stringSessionEntry -> {
			Session session = stringSessionEntry.getValue();
			session.getCtx().channel().writeAndFlush("");
		});

	}

}
