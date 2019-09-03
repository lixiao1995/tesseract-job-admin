package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: long型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:04
 */
public class LongResolve implements IResolve<Long> {

    @Override
    public Class<Long> getResolveClass() {
        return Long.class;
    }

    @Override
    public Long resolve(String res) {
        return Long.valueOf(res);
    }

}
