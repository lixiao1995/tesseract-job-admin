package nickle.tesseract;

import admin.TesseractAdminApplication;
import admin.core.event.RetryEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import tesseract.core.dto.TesseractAdminJobNotify;

/**
 * @projectName: tesseract-job-admin
 * @className: RetryTest
 * @description:
 * @author: liangxuekai
 * @createDate: 2019-07-22 10:11
 * @updateUser: liangxuekai
 * @updateDate: 2019-07-22 10:11
 * @updateRemark: 修改内容
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TesseractAdminApplication.class)
public class RetryTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void demo1(){
        TesseractAdminJobNotify tesseractAdminJobNotify  = new TesseractAdminJobNotify();
        tesseractAdminJobNotify.setJobId(1);
        tesseractAdminJobNotify.setLogId(1L);
        tesseractAdminJobNotify.setExecutorDetailId(1);
        tesseractAdminJobNotify.setTriggerId(1);
//        applicationContext.publishEvent(new RetryEvent(tesseractAdminJobNotify));
        while (true){

        }
    }
}
