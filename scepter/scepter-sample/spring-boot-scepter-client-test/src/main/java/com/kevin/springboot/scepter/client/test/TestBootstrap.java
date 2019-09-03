package com.kevin.springboot.scepter.client.test;

import com.kevin.scepter.client.core.context.Global;
import com.kevin.springboot.scepter.client.test.command.ITestCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author: kevin
 * @description: 启动类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 15:46
 */
@SpringBootApplication
public class TestBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBootstrap.class);

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = SpringApplication.run(TestBootstrap.class, args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            LOGGER.warn("Spring Boot 使用profile为:{}", profile);
        }

        Thread.sleep(5000);
        while (true) {
            try {
                String json = "{'deviceCode':'asdfsdfsd','userUnique':'asdfsdfsd'}";
                String result = Global.getInstance().resolve(ITestCommand.class).getTask(json);
                System.out.println("getTask接口返回的数据为:" + result);
                Global.getInstance().resolve(ITestCommand.class).sayHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(2000L);
        }
    }


}
