package com.kevin.scepter.client.resolve;

import com.alibaba.fastjson.JSONArray;
import com.kevin.message.protocol.utility.FastJsonHelper;

/**
 * @author: kevin
 * @description: json数组处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:03
 */
public class JSONArrayResolve implements IResolve<JSONArray> {

    private static final JSONArray EMPTY_OBJECT = new JSONArray();

    @Override
    public Class<JSONArray> getResolveClass() {
        return JSONArray.class;
    }

    @Override
    public JSONArray resolve(String res) {
        if (res == null) {
            return EMPTY_OBJECT;
        }

        return FastJsonHelper.toJsonArray(res);
    }

}
