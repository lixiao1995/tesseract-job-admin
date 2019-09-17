package admin.core.scheduler.router.impl;

import admin.core.scheduler.router.IScheduleRouter;
import admin.entity.TesseractExecutorDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * hash散列发送
 */
@Slf4j
public class HashRouter implements IScheduleRouter {

    @Override
    public TesseractExecutorDetail routerExecutor(List<TesseractExecutorDetail> tesseractExecutorList) {
        int hashCode = UUID.randomUUID().toString().hashCode();
        return tesseractExecutorList.get(((hashCode ^ (hashCode >>> 16)) >>> 1) % tesseractExecutorList.size());
    }
}
