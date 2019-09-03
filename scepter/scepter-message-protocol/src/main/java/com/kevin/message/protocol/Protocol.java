package com.kevin.message.protocol;


import com.kevin.message.protocol.enums.CompressType;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.MessageType;
import com.kevin.message.protocol.enums.PlatformType;
import com.kevin.message.protocol.enums.SecurityType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.serialize.ISerialize;
import com.kevin.message.protocol.serialize.impl.JSONSerialize;
import com.kevin.message.protocol.utility.ByteConverter;

import java.io.Serializable;

/**
 * 版本一协义定义
 * 1byte(版本号) | 4byte(协义总长度) | 4byte(序列号) | 1byte 消息来源方 | 1byte(消息体类型) | 1byte 压缩算法 | 1byte加密类型 | 1byte 序列化 | 1byte 平台(java,c ...) | n byte消息体 | 5byte(分界符)
 * 0			   1~4			      5~8             9                 10          	    11    	        12			   13			 14
 * 消息头总长度:15byte
 * <p>
 * 协义总长度 = 消息头总长度 + 消息体总长度 (不包括分界符)
 * <p>
 * 尾分界符: 9, 10, 13, 17, 18
 *
 * @author Kevin
 */
public class Protocol implements Serializable {

    /**
     * 消息总长度
     */
    private int totalLen;

    /**
     * messageId
     */
    private int messageId;

    /**
     * 消息来源
     */
    private MessageFromType fromType;

    /**
     * 消息类型
     */
    private MessageType messageType;

    /**
     * 压缩算法类型
     */
    private CompressType compressType;

    /**
     * 加密类型
     */
    private SecurityType securityType;

    /**
     * 序列化类型
     */
    private SerializeType serializeType;

    /**
     * 平台类型
     */
    private PlatformType platformType;

    /**
     * 会话对象
     */
    private Object entity;

    public Protocol(int messageId, MessageFromType fromType, MessageType messageType, CompressType compressType, SecurityType securityType, SerializeType serializeType, PlatformType platformType, Object entity) {
        this.entity = entity;
        this.messageId = messageId;
        this.fromType = fromType;
        this.messageType = messageType;
        this.compressType = compressType;
        this.securityType = securityType;
        this.serializeType = serializeType;
        this.platformType = platformType;
    }

    public Protocol(int messageId, MessageFromType fromType, Object entity, SerializeType serializeType) {
        this(messageId, fromType, entity != null ? MessageType.getMessageType(entity) : MessageType.Exception, CompressType.UnCompress, SecurityType.UnSecurity, serializeType, PlatformType.Java, entity);
    }

//    public Protocol(int messageId, MessageFromType fromType, Object entity) {
//        this(messageId, fromType, entity != null ? MessageType.getMessageType(entity) : MessageType.Exception, CompressType.UnCompress, SecurityType.UnSecurity, SerializeType.JSON, PlatformType.Java, entity);
//    }

    /**
     * 默认构造函数
     */
    public Protocol() {
        this(0, MessageFromType.UNKNOW, null, SerializeType.JSON);
    }

    /**
     * 组装发送协议内容
     *
     * @return byte[]
     * @throws Exception
     */
    public byte[] toBytes() throws Exception {
        int startIndex = 0;

        this.messageType = MessageType.getMessageType(this.entity);
        //序列化
        byte[] entityData = this.serializeType.getSerialize().serialize(this.entity);
        //加密
        entityData = this.securityType.getSecurity().encode(entityData);
        //压缩
        entityData = this.compressType.getCompress().zip(entityData);
        this.totalLen = ProtocolConst.HEAD_STACK_LENGTH + entityData.length;

        byte[] data = new byte[this.totalLen];
        data[0] = ProtocolConst.VERSION;

        startIndex += ProtocolConst.VERSION_SIZE;
        System.arraycopy(ByteConverter.intToBytesLittleEndian(this.totalLen), 0, data, startIndex, ProtocolConst.TOTAL_LENGTH_SIZE);

        startIndex += ProtocolConst.TOTAL_LENGTH_SIZE;
        System.arraycopy(ByteConverter.intToBytesLittleEndian(this.messageId), 0, data, startIndex, ProtocolConst.MESSAGE_ID_SIZE);

        startIndex += ProtocolConst.MESSAGE_ID_SIZE;
        data[startIndex] = (byte) this.messageType.getCode();

        startIndex += ProtocolConst.MESSAGE_TYPE_SIZE;
        data[startIndex] = (byte) this.fromType.getCode();

        startIndex += ProtocolConst.MESSAGE_FROM_TYPE_SIZE;
        data[startIndex] = (byte) this.compressType.getCode();

        startIndex += ProtocolConst.COMPRESS_TYPE_SIZE;
        data[startIndex] = (byte) this.securityType.getCode();

        startIndex += ProtocolConst.SECURITY_TYPE_SIZE;
        data[startIndex] = (byte) this.serializeType.getCode();

        startIndex += ProtocolConst.SERIALIZE_TYPE_SIZE;
        data[startIndex] = (byte) this.platformType.getCode();

        startIndex += ProtocolConst.PLATFORM_SIZE;
        System.arraycopy(entityData, 0, data, startIndex, this.totalLen - startIndex);

        return data;
    }

    /**
     * 读取协议信息并且组装Protocol对象
     *
     * @param data - 协议内容
     * @return Protocol
     * @throws Exception
     */
    public void fromBytes(byte[] data) throws Exception {
        int startIndex = 0;
        if (data[startIndex] != ProtocolConst.VERSION) {
            throw new Exception("协义版本错误");
        }
        // 1
        startIndex += ProtocolConst.VERSION_SIZE;
        byte[] totalLengthByte = new byte[ProtocolConst.TOTAL_LENGTH_SIZE];
        for (int i = 0; i < ProtocolConst.TOTAL_LENGTH_SIZE; i++) {
            totalLengthByte[i] = data[startIndex + i];
        }
        this.totalLen = ByteConverter.bytesToIntLittleEndian(totalLengthByte);
        // 5
        startIndex += ProtocolConst.TOTAL_LENGTH_SIZE;
        byte[] messageIDByte = new byte[ProtocolConst.MESSAGE_ID_SIZE];
        for (int i = 0; i < ProtocolConst.MESSAGE_ID_SIZE; i++) {
            messageIDByte[i] = data[startIndex + i];
        }
        this.messageId = ByteConverter.bytesToIntLittleEndian(messageIDByte);

        // 9
        startIndex += ProtocolConst.MESSAGE_ID_SIZE;
        this.messageType = MessageType.getMessageType(data[startIndex]);

        // 10
        startIndex += ProtocolConst.MESSAGE_TYPE_SIZE;
        this.fromType = MessageFromType.getMessageFromType(data[startIndex]);

        // 11
        startIndex += ProtocolConst.MESSAGE_FROM_TYPE_SIZE;
        this.compressType = CompressType.getCompressType(data[startIndex]);

        // 12
        startIndex += ProtocolConst.COMPRESS_TYPE_SIZE;
        this.securityType = SecurityType.getSecurityType(data[startIndex]);

        // 13
        startIndex += ProtocolConst.SECURITY_TYPE_SIZE;
        this.serializeType = SerializeType.getSerializeType(data[startIndex]);

        // 14
        startIndex += ProtocolConst.SERIALIZE_TYPE_SIZE;
        this.platformType = PlatformType.getPlatformType(data[startIndex]);

        // 15
        startIndex += ProtocolConst.PLATFORM_SIZE;

        byte[] entityData = new byte[data.length - startIndex];
        System.arraycopy(data, startIndex, entityData, 0, data.length - startIndex);
        //解压缩
        entityData = this.compressType.getCompress().unzip(entityData);
        //解密
        entityData = this.securityType.getSecurity().decode(entityData);

        ISerialize serialize = this.serializeType.getSerialize();
        this.entity = serialize.deserialize(entityData, messageType.getMessageType());
    }

    public int getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(int totalLen) {
        this.totalLen = totalLen;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(SecurityType securityType) {
        this.securityType = securityType;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public MessageFromType getFromType() {
        return fromType;
    }

    public void setFromType(MessageFromType fromType) {
        this.fromType = fromType;
    }

}
