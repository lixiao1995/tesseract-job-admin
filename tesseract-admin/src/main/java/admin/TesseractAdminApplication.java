package admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("admin.mapper")
public class TesseractAdminApplication {

    public static void main(String[] args) {
            SpringApplication.run(TesseractAdminApplication.class, args);
    }

}
