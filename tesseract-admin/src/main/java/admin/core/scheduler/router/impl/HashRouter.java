package admin.core.scheduler.router.impl;

import admin.core.scheduler.router.IScheduleRouter;
import admin.entity.TesseractExecutorDetail;

import java.util.List;

/**
 * hash散列发送
 */
public class HashRouter implements IScheduleRouter {

    @Override
    public TesseractExecutorDetail routerExecutor(List<TesseractExecutorDetail> tesseractExecutorList) {
        int hashCode = tesseractExecutorList.hashCode();
        return tesseractExecutorList.get((hashCode ^ (hashCode >>> 16)) % tesseractExecutorList.size());
    }
}
