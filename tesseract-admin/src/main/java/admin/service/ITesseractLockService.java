package admin.service;

import admin.entity.TesseractLock;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
public interface ITesseractLockService extends IService<TesseractLock> {
    /**
     * 采用数据库行锁来执行分布式锁
     * 注意：
     * 目前锁包含两个：
     * 1、组下面executor的锁
     * 2、组下面触发器的锁
     *
     * @param lockName
     * @param groupName
     * @return
     */
    TesseractLock lock(String lockName, String groupName);
}
