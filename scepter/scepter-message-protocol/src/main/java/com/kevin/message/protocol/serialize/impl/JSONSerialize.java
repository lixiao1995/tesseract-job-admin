package com.kevin.message.protocol.serialize.impl;

import com.kevin.message.protocol.serialize.ISerialize;
import com.kevin.message.protocol.utility.FastJsonHelper;

import java.nio.charset.Charset;

/**
 * @author: kevin
 * @description: JSON序列化接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:04
 */
public class JSONSerialize implements ISerialize {

    private Charset utf8 = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(Object obj) throws Exception {
        return FastJsonHelper.toJson(obj).getBytes(utf8);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return FastJsonHelper.toObject(new String(bytes, utf8), clazz);
    }

}
