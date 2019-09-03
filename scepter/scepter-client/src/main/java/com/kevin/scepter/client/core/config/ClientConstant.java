package com.kevin.scepter.client.core.config;

/**
 * @author: kevin
 * @description: 客户端常量
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 10:47
 */
public final class ClientConstant {

    /**
     * DEX包路径
     */
    public static final String DEX_PATH = "/data/local/tmp";

    /**
     * DEX包解压路径
     */
    public static final String CACHE_PATH = "/data/local/tmp/cache/";

    /**
     * 最大的消息ID
     */
    public static final long MAX_MESSAGE_ID = 1024 * 1024 * 1024;

    private ClientConstant() {

    }

}
