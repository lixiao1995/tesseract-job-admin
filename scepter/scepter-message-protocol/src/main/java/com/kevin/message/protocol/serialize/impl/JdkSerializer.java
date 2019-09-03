package com.kevin.message.protocol.serialize.impl;

import com.kevin.message.protocol.serialize.ISerialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author: kevin
 * @description: JDK自带序列化
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-08-14 10:56
 */
public class JdkSerializer implements ISerialize {

    @Override
    public  byte[] serialize(Object obj) {

        try {
            ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
            ObjectOutputStream out=new ObjectOutputStream(byteArr);
            out.writeObject(obj);
            out.flush();
            byte[] data = byteArr.toByteArray();
            out.close();
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ObjectInputStream input=new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object readObject = input.readObject();
            input.close();
            return (T)readObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
