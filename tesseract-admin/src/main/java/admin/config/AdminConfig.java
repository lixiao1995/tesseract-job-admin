package admin.config;

import admin.core.listener.MailListener;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.TesseractScheduleBoot;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import feign.Feign;
import feign.Request;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feignService.IAdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients
@Import(FeignClientsConfiguration.class)
public class AdminConfig {
    @Autowired
    private Decoder decoder;
    @Autowired
    private Encoder encoder;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 启动器
     *
     * @return
     */
    @Bean(initMethod = "init", destroyMethod = "destroy")
    public TesseractScheduleBoot tesseractScheduler() {
        return new TesseractScheduleBoot();
    }


    /**
     * 配置feign服务
     *
     * @return
     */
    @Bean
    public IAdminFeignService iAdminFeignService() {
        Request.Options options = new Request.Options(3 * 1000, 3 * 1000, true);
        IAdminFeignService iAdminFeignService = Feign.builder().encoder(encoder).decoder(decoder).options(options)
                .target(Target.EmptyTarget.create(IAdminFeignService.class));
        return iAdminFeignService;
    }

    /**
     * eventBus
     *
     * @return
     */
    @Bean
    public EventBus mailEventBus() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10
                , 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        AsyncEventBus asyncEventBus = new AsyncEventBus("mailEventBus", threadPoolExecutor);
        asyncEventBus.register(new MailListener(mailSender));
        return asyncEventBus;
    }

    /**
     * freemarker
     *
     * @return
     */
    @Bean
    public freemarker.template.Configuration freeMarkerConfiguration() {
        FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        freeMarkerConfigurationFactoryBean.setTemplateLoaderPath("classpath:mailTemplate");
        return freeMarkerConfigurationFactoryBean.getObject();
    }

    @Bean
    public TesseractMailTemplate tesseractMailTemplate(freemarker.template.Configuration configuration) {
        return new TesseractMailTemplate(configuration);
    }


    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
