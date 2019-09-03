package com.kevin.scepter.client.resolve;

/**
 * @author: kevin
 * @description: 字节类型处理器
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-30 11:00
 */
public class ByteResolve implements IResolve<Byte> {

    @Override
    public Class<Byte> getResolveClass() {
        return Byte.class;
    }

    @Override
    public Byte resolve(String res) {
        return Byte.valueOf(res);
    }

}
