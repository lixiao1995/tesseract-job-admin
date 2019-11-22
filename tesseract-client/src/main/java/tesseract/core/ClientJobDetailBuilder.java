package tesseract.core;

import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.annotation.TesseractJob;
import tesseract.core.handler.JobHandler;
import tesseract.exception.TesseractException;

/**
 * 构建Job Detail
 *
 * @author nickle
 */
public class ClientJobDetailBuilder {
    public static ClientJobDetail build(Class<?> clazz) {
        if (clazz.isInterface()) {
            throw new TesseractException("job detail 不允许为接口");
        }
        TesseractJob tesseractJob = clazz.getAnnotation(TesseractJob.class);
        if (tesseractJob == null) {
            throw new TesseractException("job detail tesseractJob注解不允许为空");
        }
        if (!JobHandler.class.isAssignableFrom(clazz)) {
            throw new TesseractException("job detail 必须实现JobHandler接口");
        }
        ClientJobDetail clientJobDetail = new ClientJobDetail();
        clientJobDetail.setClassName(clazz.getCanonicalName());
        clientJobDetail.setTriggerName(tesseractJob.triggerName());
        return clientJobDetail;
    }
}
