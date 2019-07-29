package tesseract;

import org.springframework.context.annotation.Import;
import tesseract.config.ExecutorConfig;
import tesseract.core.TesseractJobDetailRegistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({TesseractJobDetailRegistrar.class, ExecutorConfig.class})
public @interface EnableTesseractJob {
    String basePackage();
}
