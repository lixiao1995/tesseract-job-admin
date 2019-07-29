package tesseract.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import tesseract.EnableTesseractJob;
import tesseract.config.ExecutorConfig;

@SpringBootApplication
@EnableTesseractJob
public class SampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
