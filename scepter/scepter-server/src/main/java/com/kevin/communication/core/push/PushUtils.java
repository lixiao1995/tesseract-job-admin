package com.kevin.communication.core.push;

import com.kevin.communication.core.session.Session;
import com.kevin.communication.core.session.SessionManager;
import com.kevin.message.protocol.Protocol;
import io.netty.channel.ChannelFuture;

import java.util.Map;

/**
 * @author: kevin
 * @description: 添加注解，暂时使用工具类的方式，后面需要从业务代码中解耦出来
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-07 10:41
 */
public class PushUtils {

    public static void pushAll(Object o){
        Map<String, Session> sessionMap = SessionManager.getInstance().getSessionMap();
        sessionMap.entrySet().stream().forEach(stringSessionEntry -> {
            Session session = stringSessionEntry.getValue();
            Protocol protocol = new Protocol();
            session.getCtx().channel().writeAndFlush(protocol);
        });
    }
}
