package admin.config;

import admin.security.UserAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class CorsConfig extends WebMvcConfigurationSupport {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO 该拦截器的功能，security已经有实现，暂时不配置
        registry.addInterceptor(userAuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/tesseract-user/login**");
        super.addInterceptors(registry);
    }

    @Bean
    public UserAuthInterceptor userAuthInterceptor() {
        return new UserAuthInterceptor();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .maxAge(3600);
    }


}