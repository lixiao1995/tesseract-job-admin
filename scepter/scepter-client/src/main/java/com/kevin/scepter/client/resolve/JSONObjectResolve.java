package com.kevin.scepter.client.resolve;

import com.alibaba.fastjson.JSONObject;
import com.kevin.message.protocol.utility.FastJsonHelper;

/**
 * @author: kevin
 * @description: JSON类处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:03
 */
public class JSONObjectResolve implements IResolve<JSONObject> {

    private static final JSONObject EMPTY_OBJECT = new JSONObject();

    @Override
    public Class<JSONObject> getResolveClass() {
        return JSONObject.class;
    }

    @Override
    public JSONObject resolve(String res) {
        if (res == null) {
            return EMPTY_OBJECT;
        }

        return FastJsonHelper.toJsonObject(res);
    }

}
