package admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("admin.mapper")
@EnableCaching
public class TesseractAdminApplication {

    public static void main(String[] args) {
            SpringApplication.run(TesseractAdminApplication.class, args);
    }

}
