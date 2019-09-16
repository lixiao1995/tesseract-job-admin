package admin.config;

import admin.core.component.SenderDelegateBuilder;
import admin.core.listener.MailListener;
import admin.core.listener.RetryListener;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scheduler.TesseractScheduleBoot;
import admin.service.*;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import admin.core.scheduler.service.ITaskService;
import admin.core.scheduler.service.impl.TaskServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import tesseract.core.serializer.HessianSerializerService;
import tesseract.core.serializer.ISerializerService;

import java.util.concurrent.*;

@Configuration
public class AdminConfig {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    ApplicationContext applicationContext;

    @Value("${spring.mail.username}")
    private String from;

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
     * 序列化
     *
     * @return
     */
    @Bean
    public ISerializerService serializerService() {
        return new HessianSerializerService();
    }

    /**
     * 配置feign服务
     *
     * @return
     */
    @Bean
    public ITaskService iAdminFeignService() {
        return new TaskServiceImpl();
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
        ITesseractTriggerService tesseractTriggerService = applicationContext.getBean(ITesseractTriggerService.class);
        ITesseractFiredJobService tesseractFiredJobService = applicationContext.getBean(ITesseractFiredJobService.class);
        ITesseractJobDetailService tesseractJobDetailService = applicationContext.getBean(ITesseractJobDetailService.class);
        ITesseractExecutorDetailService tesseractExecutorDetailService = applicationContext.getBean(ITesseractExecutorDetailService.class);
        SenderDelegateBuilder senderDelegateBuilder = applicationContext.getBean(SenderDelegateBuilder.class);
        ITesseractLogService tesseractLogService = applicationContext.getBean(ITesseractLogService.class);
        asyncEventBus.register(
                new RetryListener(
                        tesseractTriggerService,
                        tesseractFiredJobService,
                        tesseractJobDetailService,
                        tesseractExecutorDetailService,
                        senderDelegateBuilder,
                        tesseractLogService));
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
