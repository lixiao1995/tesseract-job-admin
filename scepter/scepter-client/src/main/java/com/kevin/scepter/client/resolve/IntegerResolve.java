package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 整型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:01
 */
public class IntegerResolve implements IResolve<Integer> {

    @Override
    public Class<Integer> getResolveClass() {
        return Integer.class;
    }

    @Override
    public Integer resolve(String res) {
        return Integer.valueOf(res);
    }

}
