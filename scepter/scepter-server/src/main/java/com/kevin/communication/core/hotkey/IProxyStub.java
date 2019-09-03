package com.kevin.communication.core.hotkey;

import com.kevin.message.protocol.exception.ServiceFrameException;
import com.kevin.communication.core.context.BeatContext;

/**
 * @author: kevin
 * @description: 代理实现类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 19:00
 */
public interface IProxyStub {

    /**
     * 调用具体的代理方法
     *
     * @param context - BeatContext
     * @throws ServiceFrameException
     */
    public void invoke(BeatContext context) throws ServiceFrameException;

}
