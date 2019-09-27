package admin.core.scheduler.router.impl;

import admin.core.scheduler.router.IScheduleRouter;
import admin.entity.TesseractExecutorDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class FirstRouter implements IScheduleRouter {
    @Override
    public TesseractExecutorDetail routerExecutor(List<TesseractExecutorDetail> tesseractExecutorTriggerLink) {
        TesseractExecutorDetail tesseractExecutorDetail = tesseractExecutorTriggerLink.get(0);
        log.info("FirstRouter,选取:{}", tesseractExecutorDetail);
        return tesseractExecutorDetail;
    }
}
