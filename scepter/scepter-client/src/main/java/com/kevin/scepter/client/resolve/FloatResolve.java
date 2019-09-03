package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 浮点型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:01
 */
public class FloatResolve implements IResolve<Float> {

    @Override
    public Class<Float> getResolveClass() {
        return Float.class;
    }

    @Override
    public Float resolve(String res) {
        return Float.valueOf(res);
    }

}
