package nickle.tesseract;

import admin.TesseractAdminApplication;
import admin.core.event.RetryEvent;
import admin.entity.TesseractJobDetail;
import admin.entity.TesseractTrigger;
import admin.service.ITesseractJobDetailService;
import admin.service.ITesseractTriggerService;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import tesseract.core.dto.TesseractAdminJobNotify;

import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private ITesseractJobDetailService jobDetailService;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;
    @Test
    public void demo1() {
        TesseractAdminJobNotify tesseractAdminJobNotify = new TesseractAdminJobNotify();
        tesseractAdminJobNotify.setJobId(1);
        tesseractAdminJobNotify.setLogId(1L);
        tesseractAdminJobNotify.setExecutorDetailId(1);
        tesseractAdminJobNotify.setTriggerId(1);
//        applicationContext.publishEvent(new RetryEvent(tesseractAdminJobNotify));
        while (true) {

        }
    }

    @Test
    public void insertTestJob() {
        List<TesseractJobDetail> jobDetailList = new ArrayList<>(100);
        List<TesseractTrigger> triggerList = new ArrayList<>(100);
        for (int i = 0; i < 2; i++) {
            TesseractTrigger trigger = new TesseractTrigger();
            trigger.setName("testTrigger");
            trigger.setNextTriggerTime(1563954720000L);
            trigger.setPrevTriggerTime(1563954715010L);
            trigger.setCron("*/5 * * * * ?");
            trigger.setStrategy(0);
            trigger.setShardingNum(0);
            trigger.setRetryCount(3);
            trigger.setStatus(1);
            trigger.setCreator("admin");
            trigger.setExecutorName("testExecutor");
            trigger.setExecutorId(1);
            trigger.setCreateTime(1562512500000L);
            trigger.setUpdateTime(1562512500000L);
            trigger.setGroupName("defaultGroup");
            trigger.setDescription("test");
            trigger.setGroupId(2);
            triggerList.add(trigger);
        }
        tesseractTriggerService.saveBatch(triggerList);
        for (int i = 0; i < triggerList.size(); i++) {
            TesseractTrigger trigger = triggerList.get(i);
            TesseractJobDetail tesseractJobDetail = new TesseractJobDetail();
            tesseractJobDetail.setClassName("tesseract.sample.TestJob");
            tesseractJobDetail.setTriggerId(trigger.getId());
            tesseractJobDetail.setCreator("admin");
            tesseractJobDetail.setCreateTime(System.currentTimeMillis());
            jobDetailList.add(tesseractJobDetail);
        }
        jobDetailService.saveBatch(jobDetailList);
    }
}
