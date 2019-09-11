package tesseract.core.serializer;

/**
 * @description: 序列化服务
 * @author: nickle
 * @create: 2019-09-09 10:47
 **/
public interface ISerializerService {
    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes
     * @return
     */
    Object deserialize(byte[] bytes);
}
