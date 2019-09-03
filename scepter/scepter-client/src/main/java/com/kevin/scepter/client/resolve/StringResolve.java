package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 字符串解析
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:05
 */
public class StringResolve implements IResolve<String> {

    @Override
    public Class<String> getResolveClass() {
        return String.class;
    }

    @Override
    public String resolve(String res) {
        return res;
    }

}
