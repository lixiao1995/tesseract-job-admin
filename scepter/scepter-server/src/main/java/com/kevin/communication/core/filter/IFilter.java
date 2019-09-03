package com.kevin.communication.core.filter;

import com.kevin.communication.core.context.BeatContext;

/**
 * @author: kevin
 * @description: 请求过滤器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:52
 */
public interface IFilter {

    /**
     * 过滤方法
     *
     * @param context - BeatContext
     * @throws Exception
     */
    public void filter(BeatContext context) throws Exception;

    /**
     * 获得过滤器类型
     *
     * @return FilterType
     */
    public FilterType getFilterType();

}
