package nickle.tesseract;

import admin.TesseractAdminApplication;
import admin.controller.TesseractLogController;
import admin.core.mail.TesseractMailTemplate;
import admin.core.scanner.MissfireScanner;
import admin.service.*;
import com.google.common.eventbus.EventBus;
import feignService.IAdminFeignService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TesseractAdminApplication.class)
public class TesseractApplicationTests {
    @Autowired
    private ITesseractLogService logService;
    @Autowired
    private TesseractLogController logController;

    @Autowired
    private ITesseractTriggerService tesseractTriggerService;

    @Autowired
    private ITesseractExecutorDetailService executorDetailService;

    @Autowired
    private ITesseractJobDetailService tesseractJobDetailService;
    @Autowired
    private ITesseractLogService tesseractLogService;

    @Autowired
    private ITesseractExecutorService executorService;

    @Autowired
    private ITesseractFiredJobService firedJobService;

    @Autowired
    private IAdminFeignService feignService;

    @Autowired
    private ITesseractGroupService groupService;

    @Autowired
    private TesseractMailTemplate mailTemplate;


    @Autowired
    @Qualifier("mailEventBus")
    private EventBus mailEventBus;

    @Test
    public void testTesseractLogService() {
        System.out.println(logService);
    }

    @Test
    public void testTesseractLogController() {
        System.out.println(logController.logService);
    }

    @Test
    public void testMailTemplate() throws Exception {
        System.out.println(mailTemplate.getConfiguration().getTemplate("logTemplate.html"));
    }

    @Test
    public void testMissFireThread() throws InterruptedException {
        MissfireScanner missfireScanner = new MissfireScanner(tesseractTriggerService);
        missfireScanner.startThread();
        missfireScanner.join();
    }
}
