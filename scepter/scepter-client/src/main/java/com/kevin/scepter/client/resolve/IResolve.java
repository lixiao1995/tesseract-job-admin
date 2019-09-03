package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 基础的解析接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:02
 */
public interface IResolve<T> {

    /**
     * 获得需要解析的Class
     *
     * @return Class<T>
     */
    public Class<T> getResolveClass();

    /**
     * 解析，返回真正的实体
     *
     * @param res - String
     * @return T
     */
    public T resolve(String res);

}
