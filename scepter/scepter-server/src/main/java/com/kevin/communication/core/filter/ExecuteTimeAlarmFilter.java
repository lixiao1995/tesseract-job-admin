package com.kevin.communication.core.filter;

import com.kevin.communication.core.config.SocketServerConfig;
import com.kevin.communication.core.context.BeatContext;
import com.kevin.communication.core.context.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 代码执行时间告警输出过滤器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:52
 */
public class ExecuteTimeAlarmFilter implements IFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteTimeAlarmFilter.class);

    @Override
    public void filter(BeatContext context) throws Exception {
        SocketServerConfig config = Global.getInstance().getServiceConfig();
        if (config.isSlowMethod()) {
            long cost = context.getInvokeEndTime() - context.getInvokeBeginTime();
            //调用超过指定毫秒，打印日志
            if (cost >= config.getSlowMethodMillis()) {
                String msg = "time:" + cost + "ms, remoteIP:" + context.getRemoteIP() + ", mapping:" + context.getMapping();

                LOGGER.warn(msg);
            }
        }
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.Response;
    }

}
