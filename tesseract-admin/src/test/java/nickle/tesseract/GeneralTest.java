package nickle.tesseract;

import admin.core.scheduler.TesseractFutureTask;
import admin.core.scheduler.router.impl.LoadFactorRouter;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractLog;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

import static admin.constant.AdminConstant.DEFAULT_PASSWORD;

/**
 * 〈〉
 *
 * @author nickel
 * @create 2019/7/7
 * @since 1.0.0
 */

public class GeneralTest {

    @Test
    public void test() throws Exception {
        TesseractFutureTask<String> tesseractFutureTask = new TesseractFutureTask<>();
        tesseractFutureTask.lock();
        tesseractFutureTask.get();
        System.out.println(1);
    }

    @Test
    public void testLocalDate() {
        LocalDate now = LocalDate.now();
        LocalDate plus = now.plus(1, ChronoUnit.DAYS);
        LocalDate minus = now.minus(6, ChronoUnit.DAYS);
        System.out.println(Period.between(plus, minus).getDays());
        System.out.println(plus.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println(now.minus(7, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }

    @Test
    public void testStatisticsLocalDate() {
        LinkedHashMap<String, Integer> linkedHashMap = Maps.newLinkedHashMap();
        LocalDate startDate = LocalDate.now().minusDays(7);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            linkedHashMap.put(startDate.format(dateTimeFormatter), 0);
            startDate = startDate.plusDays(1);
        }
        System.out.println(linkedHashMap.keySet());
    }

    @Test
    public void testPass() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.matches("666666","$2a$10$gm4Q2j0qsTV.6CQnHiPZ4uqmMpg7QDcOe/wA/6cSB5M24tVM7W51S"));

    }

    @Test
    public void testFunction() {
        TesseractLog log = new TesseractLog();
        Function<TesseractLog, String> getClassName = TesseractLog::getClassName;
        System.out.println(getClassName.apply(log));
    }

    @Test
    public void testRouter() {
        LoadFactorRouter loadFactorRouter = new LoadFactorRouter();
        List<TesseractExecutorDetail> tesseractExecutorDetailList = Lists.newArrayList();
        TesseractExecutorDetail tesseractExecutorDetail = new TesseractExecutorDetail();
        //tesseractExecutorDetail.setLoadFactor(100D);
        TesseractExecutorDetail tesseractExecutorDetail1 = new TesseractExecutorDetail();
        //tesseractExecutorDetail1.setLoadFactor(200D);
        tesseractExecutorDetailList.add(tesseractExecutorDetail);
        tesseractExecutorDetailList.add(tesseractExecutorDetail1);
        loadFactorRouter.routerExecutor(tesseractExecutorDetailList);
        System.out.println(tesseractExecutorDetailList);
    }


    @Test
    public void testFreeMarker() throws Exception {
        FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean = new FreeMarkerConfigurationFactoryBean();
        freeMarkerConfigurationFactoryBean.setTemplateLoaderPath("classpath:mailTemplate");
        freeMarkerConfigurationFactoryBean.afterPropertiesSet();
        Configuration configuration = freeMarkerConfigurationFactoryBean.getObject();
        String string = FreeMarkerTemplateUtils.processTemplateIntoString(
                configuration.getTemplate("missfireTemplate.html"), Maps.newHashMap());
        System.out.println(string);
    }
}
