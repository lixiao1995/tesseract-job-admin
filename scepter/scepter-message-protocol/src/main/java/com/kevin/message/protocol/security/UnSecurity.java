package com.kevin.message.protocol.security;

/**
 * @author: kevin
 * @description: 不做任何加解密处理
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:02
 */
public class UnSecurity implements ISecurity {

    @Override
    public byte[] decode(byte[] bytes) throws Exception {
        return bytes;
    }

    @Override
    public byte[] encode(byte[] bytes) throws Exception {
        return bytes;
    }

}
