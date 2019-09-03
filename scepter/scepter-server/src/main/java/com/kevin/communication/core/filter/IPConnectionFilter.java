package com.kevin.communication.core.filter;

import com.kevin.communication.utils.IPTable;
import com.kevin.communication.core.context.BeatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: kevin
 * @description: IP过滤过滤器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:53
 */
public class IPConnectionFilter implements IFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPConnectionFilter.class);

    @Override
    public void filter(BeatContext context) throws Exception {
        String ip = context.getRemoteIP();

        if (IPTable.isAllow(ip)) {
            LOGGER.info("new channel conected:" + ip);
        } else {
            LOGGER.error("forbid ip not allow connect:" + ip);
            context.getSession().close();
        }
    }

    @Override
    public FilterType getFilterType() {
        return FilterType.CONNECTION;
    }

}
