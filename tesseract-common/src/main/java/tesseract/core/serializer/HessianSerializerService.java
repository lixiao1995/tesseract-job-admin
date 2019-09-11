package tesseract.core.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import tesseract.exception.TesseractException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description: hessian序列化实现
 * @author: nickle
 * @create: 2019-09-09 10:53
 **/
public class HessianSerializerService implements ISerializerService {
    @Override
    public byte[] serialize(Object obj) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {

            throw new TesseractException("序列化异常");
        } finally {
            try {
                hessianOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Object deserialize(byte[] bytes) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
           e.printStackTrace();
            throw new TesseractException("反序列化异常");
        } finally {
            try {
                hessianInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
