package com.kevin.message.protocol.security;

/**
 * @ClassName: ISecurity
 * @Description: 数据加密/解密接口
 * @Author: Kevin
 * @CreateDate: 18/11/1 下午6:21
 * @UpdateUser:
 * @UpdateDate: 18/11/1 下午6:21
 * @UpdateRemark: 更新项目
 * @Version: 1.0
 */
/**
 * @author: kevin
 * @description: 数据加密/解密接口
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:00
 */
public interface ISecurity {

    /**
     * 解密
     *
     * @param bytes - byte[]
     * @return byte[]
     * @throws Exception
     */
    public byte[] decode(byte[] bytes) throws Exception;

    /**
     * 加密
     *
     * @param bytes - byte[]
     * @return byte[]
     * @throws Exception
     */
    public byte[] encode(byte[] bytes) throws Exception;

}
