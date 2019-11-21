package tesseract.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明这是一个Tesseract Job将会被收集注册到服务端
 *
 * @author nickle
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TesseractJob {
    /**
     * 触发器名字
     *
     * @return
     */
    String triggerName();
}
