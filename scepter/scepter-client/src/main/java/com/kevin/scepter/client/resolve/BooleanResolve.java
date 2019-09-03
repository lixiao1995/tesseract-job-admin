package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: boolean类型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:00
 */
public class BooleanResolve implements IResolve<Boolean> {

    @Override
    public Class<Boolean> getResolveClass() {
        return Boolean.class;
    }

    @Override
    public Boolean resolve(String res) {
        return Boolean.valueOf(res);
    }

}
