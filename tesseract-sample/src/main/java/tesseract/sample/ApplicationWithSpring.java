package tesseract.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tesseract.EnableTesseractJob;

/**
 * spring boot工程使用
 */
@SpringBootApplication
@EnableTesseractJob
public class ApplicationWithSpring {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationWithSpring.class, args);
    }
}
