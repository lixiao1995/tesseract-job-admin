package com.kevin.scepter.client.test;

import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.scepter.client.core.ClientBootstrap;
import com.kevin.scepter.client.core.config.SocketClientConfig;
import com.kevin.scepter.client.core.context.Global;
import com.kevin.scepter.client.core.server.ClientHeartBeatHandler;
import com.kevin.scepter.client.test.command.ITestCommand;

import java.io.IOException;

public class ClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        SocketClientConfig config = new SocketClientConfig();
        config.setDeviceId("100001");
        config.setBasePackages("com.kevin.scepter.client.test.command");
        config.setServerHost("127.0.0.1");
        config.setServerPort(9527);
        config.setRequestTimeout(300);
        config.setWriterIdleTime(5);
        config.setSerializeType(SerializeType.HESSIAN2);
//        config.setClientPort(1100);
        config.setClientHeartBeatHandler(new ClientHeartBeatHandler());

        System.out.println("连接服务端开启");
        ClientBootstrap client = new ClientBootstrap(config);
        client.start();


        Thread.sleep(5000);

        while (true) {
            try {
                String json = "{'deviceCode':'asdfsdfsd','userUnique':'asdfsdfsd'}";
                String result = Global.getInstance().resolve(ITestCommand.class).getTask(json);
                System.out.println("getTask接口返回的数据为:" + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000L);
        }
    }

}
