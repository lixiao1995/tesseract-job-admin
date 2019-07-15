package admin.core.scheduler.router.impl;

import admin.core.scheduler.router.IScheduleRouter;
import admin.entity.TesseractExecutorDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * hash散列发送
 */
@Slf4j
public class HashRouter implements IScheduleRouter {

    @Override
    public TesseractExecutorDetail routerExecutor(List<TesseractExecutorDetail> tesseractExecutorList) {
        int hashCode = tesseractExecutorList.hashCode();
        return tesseractExecutorList.get(((hashCode ^ (hashCode >>> 16)) >>> 1) % tesseractExecutorList.size());
    }
}
