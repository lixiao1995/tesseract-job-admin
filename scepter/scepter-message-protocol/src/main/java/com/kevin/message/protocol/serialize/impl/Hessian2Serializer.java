package com.kevin.message.protocol.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.kevin.message.protocol.serialize.ISerialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author: kevin
 * @description: 大佬, 写点注释吧(这个类的作用)
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-14 10:47
 */
public class Hessian2Serializer implements ISerialize {

    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output ho = new Hessian2Output(os);
        try {
            ho.writeObject(obj);
            ho.flush();
            byte[] result = os.toByteArray();
            ho.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        Hessian2Input hi = new Hessian2Input(is);
        try {
            Object readObject = hi.readObject();
            hi.close();
            return (T) readObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
