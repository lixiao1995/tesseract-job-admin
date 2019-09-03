package com.kevin.communication.core.init;

import com.kevin.communication.core.config.SocketServerConfig;
import com.kevin.communication.core.context.Global;
import com.kevin.communication.core.hotkey.CommandHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 初始化
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:02
 */
public class InitCommandHolder implements IInit {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitCommandHolder.class);

    @Override
    public void init() {
        SocketServerConfig serviceConfig = Global.getInstance().getServiceConfig();

        LOGGER.info("----------------init command holder--------------------");
        CommandHolder.init(serviceConfig.getBasePackages(), serviceConfig.getProxyFactory());
        LOGGER.info("----------------init command holder finish--------------");
    }

}
