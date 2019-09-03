package com.kevin.message.protocol.serialize.impl;


import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.kevin.message.protocol.serialize.ISerialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: kevin
 * @description: Hessian序列化
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-14 10:44
 */
public class HessianSerializer implements ISerialize {

    @Override
    public byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
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
        HessianInput hi = new HessianInput(is);
        try {
            Object readObject = hi.readObject();
            hi.close();
            return (T) readObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
