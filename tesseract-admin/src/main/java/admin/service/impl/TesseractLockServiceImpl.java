package admin.service.impl;

import admin.entity.TesseractLock;
import admin.mapper.TesseractLockMapper;
import admin.service.ITesseractLockService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author nickle
 * @since 2019-07-03
 */
@Service
@Slf4j
public class TesseractLockServiceImpl extends ServiceImpl<TesseractLockMapper, TesseractLock> implements ITesseractLockService {
    private ConcurrentHashMap<String, Boolean> checkMap = new ConcurrentHashMap<>();

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TesseractLock lock(String lockName, String groupName) {
        String key = lockName + groupName;
        //检测表内是否存在锁字段,采用缓存减少对数据库的访问
        while (checkMap.get(key) == null) {
            QueryWrapper<TesseractLock> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(TesseractLock::getGroupName, groupName).eq(TesseractLock::getName, lockName);
            TesseractLock tesseractLock = getOne(queryWrapper);
            //锁不存在创建锁
            if (tesseractLock == null) {
                tesseractLock = new TesseractLock();
                tesseractLock.setName(lockName);
                tesseractLock.setGroupName(groupName);
                //并发条件下采用数据库唯一索引来解决创建多个锁
                try {
                    this.save(tesseractLock);
                    checkMap.put(key, true);
                } catch (Exception e) {
                    log.warn("锁创建异常:{}", e.getMessage());
                }
            } else {
                checkMap.put(key, true);
            }
        }
        return this.baseMapper.lock(groupName, lockName);
    }
}
