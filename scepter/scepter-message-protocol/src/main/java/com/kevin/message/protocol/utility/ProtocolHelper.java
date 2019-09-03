package com.kevin.message.protocol.utility;

import com.kevin.message.protocol.Protocol;
import com.kevin.message.protocol.ProtocolConst;
import com.kevin.message.protocol.enums.DeviceStatus;
import com.kevin.message.protocol.enums.MessageFromType;
import com.kevin.message.protocol.enums.SerializeType;
import com.kevin.message.protocol.exception.ExceptionType;
import com.kevin.message.protocol.exception.ServiceFrameException;
import com.kevin.message.protocol.message.ExceptionMessage;
import com.kevin.message.protocol.message.HeartBeatMessage;
import com.kevin.message.protocol.message.MessageIdFactory;
import com.kevin.message.protocol.message.PushMessage;
import com.kevin.message.protocol.message.RequestMessage;
import com.kevin.message.protocol.message.StatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: kevin
 * @description: 协议辅助工具类
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:07
 */
public final class ProtocolHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolHelper.class);

    /**
     * 检查头是否是指定的头
     *
     * @param buf - byte[]
     * @return true or false
     */
    public static boolean checkHeadDelimiter(byte[] buf) {
        if (buf.length == ProtocolConst.P_START_TAG.length) {
            for (int i = 0; i < buf.length; i++) {
                if (buf[i] != ProtocolConst.P_START_TAG[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 创建默认心跳消息协议
     *
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param fstConn  - 是否第一次请求
     * @return Protocol
     */
    public static Protocol createHeartBeatMessage(MessageFromType fromType, String deviceId, boolean fstConn) {
        return createHeartBeatMessage(fromType, deviceId, fstConn, SerializeType.JSON);
    }

    /**
     * 创建心跳消息协议
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param fstConn - 是否第一次请求
     * @param serializeType - 序列化协议类型
     * @return Protocol
     */
    public static Protocol createHeartBeatMessage(MessageFromType fromType, String deviceId, boolean fstConn, SerializeType serializeType) {
        HeartBeatMessage m = new HeartBeatMessage(deviceId);
        m.setDeviceId(deviceId);
        m.setMessageTime(System.currentTimeMillis());
        m.setFstConn(fstConn ? 1 : 0);
        return new Protocol(0, fromType, m, serializeType);
    }

    /**
     * 创建心跳消息协议，自定义消息
     *
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param fstConn  - 是否第一次请求
     * @return Protocol
     */
//    public static Protocol createHeartBeatMessage(MessageFromType fromType, String deviceId, boolean fstConn, HeartBeatMessage heartBeatMessage) {
//        heartBeatMessage.setDeviceId(deviceId);
//        heartBeatMessage.setMessageTime(System.currentTimeMillis());
//        heartBeatMessage.setFstConn(fstConn ? 1 : 0);
//        return new Protocol(0, fromType, heartBeatMessage);
//    }

    /**
     * 创建异常消息协议
     *
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param ex       - 异常类
     * @param serializeType - 序列化协议类型
     * @return Protocol
     */
    public static Protocol createExceptionMessage(int messageId, MessageFromType fromType, String deviceId, Throwable ex, SerializeType serializeType) {
        ExceptionMessage m = new ExceptionMessage(ExceptionType.OTHER_EXCEPTION.getCode());
        m.setDeviceId(deviceId);
        m.setMessageTime(System.currentTimeMillis());

        StringBuilder errMsg = new StringBuilder();

        errMsg.append("error time:");
        errMsg.append(System.nanoTime());

        errMsg.append("--exType:");
        errMsg.append(ExceptionType.OTHER_EXCEPTION.name());

        if (ex != null) {
            errMsg.append("--Message:");
            errMsg.append(ex.getMessage());
            errMsg.append("\n");
            errMsg.append(getStackTrace(ex));
        }

        m.setErrMsg(errMsg.toString());
        m.setFromIP("");
        m.setToIP("");

        return new Protocol(messageId, fromType, m, serializeType);
    }

    /**
     * 创建默认异常消息协议
     * 默认序列化协议为JSON
     *
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param ex       - 异常类
     * @return Protocol
     */
    public static Protocol createExceptionMessage(int messageId, MessageFromType fromType, String deviceId, Throwable ex) {
        return createExceptionMessage(messageId, fromType, deviceId, ex, SerializeType.JSON);
    }

    /**
     * 创建异常消息协议
     *
     * @param fromType - 消息来源
     * @param ex       - 异常类
     * @param serializeType - 序列化协议类型
     * @return Protocol
     */
    public static Protocol createExceptionMessage(MessageFromType fromType, ServiceFrameException ex, SerializeType serializeType) {
        ExceptionMessage m = new ExceptionMessage(ex.getExType() != null ? ex.getExType().getCode() : ExceptionType.OTHER_EXCEPTION.getCode());
        m.setDeviceId(ex.getDeviceId());
        m.setMessageTime(System.currentTimeMillis());

        StringBuilder errMsg = new StringBuilder();

        errMsg.append("error time:");
        errMsg.append(System.nanoTime());

        errMsg.append("--exType:");
        if (ex.getExType() == null) {
            errMsg.append(ExceptionType.OTHER_EXCEPTION.name());
        } else {
            errMsg.append(ex.getExType().name());
        }

        errMsg.append("--fromIP:");
        if (ex.getFromIP() != null) {
            errMsg.append(ex.getFromIP());
        }

        errMsg.append("--toIP:");
        if (ex.getToIP() != null) {
            errMsg.append(ex.getToIP());
        }

        errMsg.append("--Message:");
        if (ex.getErrorMsg() != null) {
            errMsg.append(ex.getErrorMsg());
        }
        errMsg.append("\n");
        errMsg.append("Caused by: ");
        errMsg.append(getStackTrace(ex));

        m.setErrMsg(errMsg.toString());
        m.setFromIP(ex.getFromIP());
        m.setToIP(ex.getToIP());

        return new Protocol(ex.getMessageId(), fromType, m, serializeType);
    }

    /**
     * 创建默认异常消息协议
     * 默认序列化协议为JSON
     *
     * @param fromType - 消息来源
     * @param ex       - 异常类
     * @return Protocol
     */
    public static Protocol createExceptionMessage(MessageFromType fromType, ServiceFrameException ex) {
        return createExceptionMessage(fromType, ex, SerializeType.JSON);
    }


    /**
     * 获得错误堆栈信息
     *
     * @param e - Throwable
     * @return String
     */
    private static String getStackTrace(Throwable e) {
        try (Writer writer = new StringWriter(); PrintWriter printWriter = new PrintWriter(writer)) {
            e.printStackTrace(printWriter);
            return writer.toString();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), e);
        }

        return "";
    }

    /**
     * 创建默认状态变更协议
     * 默认序列化协议为JSON
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param status   - 设备状态
     * @return Protocol
     */
    public static Protocol createStatusMessage(MessageFromType fromType, String deviceId, DeviceStatus status) {
        StatusMessage m = new StatusMessage(deviceId, status.getCode());
        m.setMessageTime(System.currentTimeMillis());

        return createStatusMessage(fromType,deviceId,status,SerializeType.JSON);
    }

    /**
     * 状态状态变更协议
     *
     * @param fromType - 消息来源
     * @param deviceId - 设备ID
     * @param status   - 设备状态
     * @param serializeType - 序列化协议类型
     * @return Protocol
     */
    public static Protocol createStatusMessage(MessageFromType fromType, String deviceId, DeviceStatus status, SerializeType serializeType) {
        StatusMessage m = new StatusMessage(deviceId, status.getCode());
        m.setMessageTime(System.currentTimeMillis());

        return new Protocol(0, fromType, m,serializeType);
    }

//    /**
//     * 创建服务端向客户端push的消息处理
//     *
//     * @param deviceId
//     * @param mapping
//     * @param data
//     * @return
//     */
//    private Protocol createPushMessage(String deviceId, String mapping, Object data) {
//        PushMessage push = new PushMessage(deviceId, mapping);
//        push.setMessageTime(System.currentTimeMillis());
//        push.setBody(FastJsonHelper.toJson(data));
//        return new Protocol(MessageIdFactory.createMessageId(), MessageFromType.SERVER, push);
//    }
    private ProtocolHelper() {

    }

}
