package com.kevin.scepter.springboot.server.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kevin.communication.core.NettyBootstrap;
import com.kevin.communication.core.config.SocketServerConfig;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.scepter.springboot.server.hotkey.SpringProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
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
 * @description: springboot-启动类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-05 14:51
 */
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnProperty(prefix = "socket.server.enable", value = "true", matchIfMissing = true)
public class ServerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfiguration.class);

    private ApplicationContext context;

    @Autowired
    private ServerProperties serverProperties;

    public ServerConfiguration(ApplicationContext applicationContext, ServerProperties serverProperties) {
        this.context = applicationContext;
        this.serverProperties = serverProperties;
    }

    /**
     * 创建SocketServer Bean
     *
     * @return SocketServer
     * @throws Exception
     */
    @Bean
    @ConditionalOnMissingBean(NettyBootstrap.class)
    public NettyBootstrap bootstrapNettyServer() throws Exception {
        LOGGER.info("basePackages : " + serverProperties.getBasePackages());
        LOGGER.info("port : " + serverProperties.getPort());

        if (serverProperties.getPort() == 0) {
            throw new RuntimeException("serverProperties port can't zero!");
        }
        if (StringUtils.isEmpty(serverProperties.getBasePackages())) {
            throw new RuntimeException("serverProperties basePackages can't empty!");
        }

        //初始化配置信息
        SocketServerConfig serviceConfig = new SocketServerConfig();
        serviceConfig.setBasePackages(serverProperties.getBasePackages());
        serviceConfig.setPort(serverProperties.getPort());
        serviceConfig.setProxyFactory(new SpringProxyFactory(context));
        serviceConfig.setReaderIdleTime(serverProperties.getReaderIdleTime());
        serviceConfig.setSlowMethod(serverProperties.isSlowMethod());
        serviceConfig.setSlowMethodMillis(serverProperties.getSlowMethodMillis());
        Integer serializeType = serverProperties.getSerializeType();
        SerializeType[] values = SerializeType.values();
        Optional<SerializeType> first = Arrays.stream(values).filter(serializeTypeValue -> serializeTypeValue.getCode() == serializeType).findFirst();
        if (first.isPresent()) {
            serviceConfig.setSerializeType(first.get());
        }
        NettyBootstrap boostrap = new NettyBootstrap(serviceConfig);

        //必须通过一个线程来启动，否则springboot启动类会被卡住
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("client-pool-%d").build();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        singleThreadPool.execute(() -> {
            try {
                boostrap.start();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });
        
        return boostrap;
    }

}
