package com.kevin.scepter.springboot.client.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.scepter.client.core.ClientBootstrap;
import com.kevin.scepter.client.core.config.SocketClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: kevin
 * @description: 客户端配置类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 15:07
 */
@Configuration
@EnableConfigurationProperties(ClientProperties.class)
@ConditionalOnProperty(prefix = "socket.client.enable", value = "true", matchIfMissing = true)
public class ClientConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientConfiguration.class);

    @Autowired
    private ClientProperties clientProperties;


    public ClientConfiguration(ClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Bean
    @ConditionalOnMissingBean(SocketClientConfig.class)
    public ClientBootstrap bootstrapNettyClient() throws Exception {
        LOGGER.info("basePackages : " + clientProperties.getBasePackages());
        LOGGER.info("port : " + clientProperties.getServerPort());

        if (clientProperties.getServerPort() == 0) {
            throw new RuntimeException("clientProperties port can't zero!");
        }
        if (StringUtils.isEmpty(clientProperties.getBasePackages())) {
            throw new RuntimeException("clientProperties basePackages can't empty!");
        }
        SocketClientConfig config = new SocketClientConfig();
        config.setDeviceId(clientProperties.getDeviceId());
        config.setBasePackages(clientProperties.getBasePackages());
        config.setServerHost(clientProperties.getServerHost());
        config.setServerPort(clientProperties.getServerPort());
        config.setRequestTimeout(clientProperties.getRequestTimeout());
        config.setWriterIdleTime(clientProperties.getWriterIdleTime());
        Integer serializeType = clientProperties.getSerializeType();
        SerializeType[] values = SerializeType.values();
        Optional<SerializeType> first = Arrays.stream(values).filter(serializeTypeValue -> serializeTypeValue.getCode() == serializeType).findFirst();
        if (first.isPresent()) {
            config.setSerializeType(first.get());
        }
        ClientBootstrap client = new ClientBootstrap(config);

        //必须通过一个线程来启动，否则springboot启动类会被卡住
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("client-pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        singleThreadPool.execute(() -> {
            try {
                client.start();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

        return client;
    }

}
