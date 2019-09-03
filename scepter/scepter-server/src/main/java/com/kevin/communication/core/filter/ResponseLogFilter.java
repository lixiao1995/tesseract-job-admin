package com.kevin.communication.core.filter;

import com.kevin.message.protocol.message.RequestMessage;
import com.kevin.message.protocol.message.ResponseMessage;
import com.kevin.communication.core.context.BeatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: 响应日志过滤
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:54
 */
public class ResponseLogFilter implements IFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLogFilter.class);

    @Override
    public void filter(BeatContext context) throws Exception {
        if (!context.isResponseFilter()) {
            return;
        }

        RequestMessage request = context.getRequest();
        ResponseMessage response = context.getResponse();

        StringBuilder buf = new StringBuilder();
        buf.append("response ---- ").append(",");
        buf.append("remoteIP:").append(context.getRemoteIP()).append(",");
        buf.append("localIP:").append(context.getLocalIP()).append(",");
        buf.append("Server.mapping:" + context.getMapping()).append(",");
        buf.append("Server.DeviceId:" + context.getDeviceId()).append(",");

        if (request != null) {
            buf.append("Server.Body:").append(request.getBody()).append(",");
        }
        if (response != null) {
            buf.append("ggr").append(response.getBody()).append(",");
        }

        LOGGER.info(buf.toString());
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.Response;
    }

}
