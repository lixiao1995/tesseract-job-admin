package admin.config;

import admin.core.component.SendToExecuteComponent;
import admin.core.listener.MailListener;
import admin.core.listener.RetryListener;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.TesseractScheduleBoot;
import admin.service.ITesseractExecutorDetailService;
import admin.service.ITesseractFiredJobService;
import admin.service.ITesseractJobDetailService;
import admin.service.ITesseractTriggerService;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import feign.Feign;
import feign.Request;
import feign.Target;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feignService.IAdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
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
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private ITesseractTriggerService tesseractTriggerService;
    @Autowired
    private ITesseractFiredJobService tesseractFiredJobService;
    @Autowired
    private ITesseractJobDetailService tesseractJobDetailService;
    @Autowired
    private ITesseractExecutorDetailService tesseractExecutorDetailService;
    @Autowired
    private SendToExecuteComponent sendToExecuteComponent;

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
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("mailEventBus-pool-%d").build();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10
//                , 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100));
        AsyncEventBus asyncEventBus = new AsyncEventBus("mailEventBus", threadPoolExecutor);
        asyncEventBus.register(new MailListener(mailSender, from));
        return asyncEventBus;
    }
    /**
     * eventBus
     *
     * @return
     */
    @Bean
    public EventBus retryEventBus() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("retryEventBus-pool-%d").build();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(5, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        AsyncEventBus asyncEventBus = new AsyncEventBus("retryEventBus", threadPoolExecutor);
        asyncEventBus.register(
                new RetryListener(
                        tesseractTriggerService,
                        tesseractFiredJobService,
                        tesseractJobDetailService,
                        tesseractExecutorDetailService,
                        sendToExecuteComponent));
        return asyncEventBus;
    }

    @Bean("tesseractConfiguration")
    public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean() {
        FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        freeMarkerConfigurationFactoryBean.setTemplateLoaderPath("classpath:mailTemplate");
        return freeMarkerConfigurationFactoryBean;
    }

    @Bean
    public TesseractMailTemplate tesseractMailTemplate(@Qualifier("tesseractConfiguration") freemarker.template.Configuration configuration) throws Exception {
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
