package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: short解析
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:04
 */
public class ShortResolve implements IResolve<Short> {

    @Override
    public Class<Short> getResolveClass() {
        return Short.class;
    }

    @Override
    public Short resolve(String res) {
        return Short.valueOf(res);
    }

}
