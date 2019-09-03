package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: double类型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:00
 */
public class DoubleResolve implements IResolve<Double> {

    @Override
    public Class<Double> getResolveClass() {
        return Double.class;
    }

    @Override
    public Double resolve(String res) {
        return Double.valueOf(res);
    }

}
