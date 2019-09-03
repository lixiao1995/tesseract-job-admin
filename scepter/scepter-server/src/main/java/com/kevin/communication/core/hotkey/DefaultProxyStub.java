package com.kevin.communication.core.hotkey;

import com.kevin.communication.resolve.ResolveFactory;
import com.kevin.communication.utils.ExceptionFactory;
import com.kevin.message.protocol.exception.ExceptionType;
import com.kevin.message.protocol.exception.ServiceFrameException;
import com.kevin.message.protocol.utility.FastJsonHelper;
import com.kevin.communication.core.context.BeatContext;

/**
 * @author: kevin
 * @description: 默认的代理类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:59
 */
public class DefaultProxyStub implements IProxyStub {

    private static final Object[] EMPTY_ARGS = new Object[]{};

    private Object target;

    public DefaultProxyStub(Object target) {
        this.target = target;
    }

    @Override
    public void invoke(BeatContext context) throws ServiceFrameException {
        CommandInfo info = context.getCommand();
        if (info == null) {
            throw ExceptionFactory.createServiceFrameException("context mapping " + context.getMapping() + " has not match Method", context, ExceptionType.NOT_FOUND_METHOD_EXCEPTION, null);
        }

        try {
            Object returnObject = info.getActionMethod().invoke(target, resolveParameters(context, info));
            if (info.getReturnType() != void.class) {
                //此处需要返回信息
                context.getResponse().setBody(FastJsonHelper.toJson(returnObject));
            }
        } catch (Exception e) {
            throw ExceptionFactory.createServiceFrameException("invoke method : " + context.getMapping() + " error!", context, ExceptionType.SERVICE_INVOKE_EXCEPTION, e);
        }
    }

    /**
     * 解析参数并且按照参数类型返回参数数组
     *
     * @param context - BeatContext
     * @param info    - CommandInfo
     * @return Object[]
     */
    private Object[] resolveParameters(BeatContext context, CommandInfo info) {
        Class<?>[] classes = info.getParamTypes();
        if (classes == null || classes.length == 0) {
            return EMPTY_ARGS;
        }
        String[] paramNames = info.getParamNames();

        ResolveFactory resolveFactory = ResolveFactory.getInstance();
        Object[] args = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            args[i] = resolveFactory.resolve(context, paramNames[i], classes[i]);
        }

        return args;
    }

}
