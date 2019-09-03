package com.kevin.message.protocol.serialize;

/**
 * @author: kevin
 * @description: 数据序列化/反序列化接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:04
 */
public interface ISerialize {

    /**
     * 序列化
     *
     * @param obj - Object
     * @return byte[]
     * @throws Exception
     */
    public byte[] serialize(Object obj) throws Exception;

    /**
     * 反序列化
     *
     * @param bytes - byte[]
     * @param clazz - 转换的Class类型
     * @return T
     * @throws Exception
     */
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;

}
