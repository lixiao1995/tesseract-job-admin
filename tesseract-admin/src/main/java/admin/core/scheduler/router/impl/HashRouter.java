package admin.core.scheduler.router.impl;

import admin.core.scheduler.router.IScheduleRouter;
import admin.entity.TesseractExecutor;
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
        //hashCode可能为负数，所以此处与一个最大值
        return tesseractExecutorList.get(tesseractExecutorList.hashCode() & Integer.MAX_VALUE % tesseractExecutorList.size());
    }
}
