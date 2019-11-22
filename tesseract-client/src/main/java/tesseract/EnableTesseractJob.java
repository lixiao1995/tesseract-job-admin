package tesseract;

import org.springframework.context.annotation.Import;
import tesseract.config.SpringExecutorConfig;
import tesseract.core.SpringTesseractJobDetailRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({SpringTesseractJobDetailRegistrar.class, SpringExecutorConfig.class})
public @interface EnableTesseractJob {
    String basePackage() default "";
}
