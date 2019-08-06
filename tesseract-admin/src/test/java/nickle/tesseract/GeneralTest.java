package nickle.tesseract;

import admin.core.scheduler.CronExpression;
import admin.core.scheduler.router.impl.LoadFactorRouter;
import admin.entity.TesseractExecutorDetail;
import admin.entity.TesseractLog;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

/**
 * 〈〉
 *
 * @author nickel
 * @create 2019/7/7
 * @since 1.0.0
 */

public class GeneralTest {
    @Test
    public void testCronExpression() throws Exception {

            Integer a = 100;
            Integer b = new Integer(100);
            int c = 100;
            Integer a1 = 200;

            System.out.print(a.equals(b));
            System.out.print(",");
            System.out.print(a == c);
            System.out.print(",");
            System.out.print(a1 == a + b);



    }


    @Test
    public void test (){
        System.out.println(StringUtils.camelToUnderline("xxlTess"));

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
        //加密"0"
        //String encode = bCryptPasswordEncoder.encode("admin");
        //System.out.println(encode);
        //结果：$2a$10$/eEV4X7hXPzYGzOLXfCizu6h7iRisp7I116wPA3P9uRcHAKJyY4TK
        System.out.println(bCryptPasswordEncoder.encode("666666"));
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
        tesseractExecutorDetail.setLoadFactor(100D);
        TesseractExecutorDetail tesseractExecutorDetail1 = new TesseractExecutorDetail();
        tesseractExecutorDetail1.setLoadFactor(200D);
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
