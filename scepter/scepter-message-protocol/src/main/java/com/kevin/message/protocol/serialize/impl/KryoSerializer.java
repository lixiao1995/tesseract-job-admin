package com.kevin.message.protocol.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.kevin.message.protocol.serialize.ISerialize;

import java.io.ByteArrayOutputStream;

/**
 * @author kevin
 * 2018年10月23日 下午3:14:31
 * impl:kryo
 */
public class KryoSerializer implements ISerialize {

    /**
     * kryo 不是线程安全的，所以使用池控制
     */
    private static final KryoPool KRYO_POOL = new KryoPool.Builder(
            () -> {
                Kryo kryo = new Kryo();
                return kryo;

            }).build();

    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Output output = new Output(out);
            Kryo kryo = KRYO_POOL.borrow();
            kryo.writeObject(output, obj);
            KRYO_POOL.release(kryo);
            output.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            Input input = new Input(bytes);
            Kryo kryo = KRYO_POOL.borrow();
            T res = kryo.readObject(input, clazz);
            KRYO_POOL.release(kryo);
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
