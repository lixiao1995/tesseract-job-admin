package com.kevin.communication.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author: kevin
 * @description: 创建sessionMap的key工具类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-06 15:21
 */
public class CreateSessionKey {


    /**
     * 通过socketAddress创建sessionMap的key
     *  如果socketAddress为null或者无法强转InetSocketAddress，则返回null
     * @param socketAddress
     * @return
     */
    public static String inetSocketAddressBySocketAddress(SocketAddress socketAddress) {
        try {
            if (socketAddress != null) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                String ip = inetSocketAddress.getAddress().getHostAddress();
                int port = inetSocketAddress.getPort();
                String key = ip + ":" + port;
                return key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过ChannelHandlerContext创建sessionMap的key
     *  如果ctx为null，则返回null
     * @param ctx
     * @return
     */
    public static String inetSocketAddressByChannelHandlerContexts(ChannelHandlerContext ctx) {
        if (ctx != null) {
            SocketAddress socketAddress = ctx.channel().remoteAddress();
            return inetSocketAddressBySocketAddress(socketAddress);
        }
       return null;
    }
}
