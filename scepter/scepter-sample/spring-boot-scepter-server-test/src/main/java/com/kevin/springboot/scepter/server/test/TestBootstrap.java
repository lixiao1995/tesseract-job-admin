package com.kevin.springboot.scepter.server.test;

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

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(TestBootstrap.class, args);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            LOGGER.warn("Spring Boot 使用profile为:{}", profile);
        }
    }


}
