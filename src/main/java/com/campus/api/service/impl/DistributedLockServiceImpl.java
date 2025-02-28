package com.campus.api.service.impl;

import com.campus.api.service.DistributedLockService;
import com.campus.api.mapper.DistributedLockMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DistributedLockServiceImpl implements DistributedLockService {

    private static final Logger log = LoggerFactory.getLogger(DistributedLockServiceImpl.class);

    @Autowired
    private DistributedLockMapper distributedLockMapper;

    @Override
    public String tryLock(String lockKey, int timeoutSeconds) {
        String lockValue = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(timeoutSeconds);
        
        try {
            // 先清理过期的锁
            distributedLockMapper.deleteExpiredLocks();
            
            // 尝试获取锁
            int result = distributedLockMapper.insertLock(lockKey, lockValue, expireTime);
            if (result > 0) {
                log.info("获取分布式锁成功: key={}, value={}", lockKey, lockValue);
                return "OK";
            }
            
            log.info("获取分布式锁失败: key={}", lockKey);
            return null;
        } catch (Exception e) {
            log.error("获取分布式锁异常: key={}, error={}", lockKey, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            int result = distributedLockMapper.deleteLock(lockKey, lockValue);
            if (result > 0) {
                log.info("释放分布式锁成功: key={}, value={}", lockKey, lockValue);
                return true;
            }
            log.warn("释放分布式锁失败，锁不存在或已过期: key={}, value={}", lockKey, lockValue);
            return false;
        } catch (Exception e) {
            log.error("释放分布式锁异常: key={}, value={}, error={}", 
                    lockKey, lockValue, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean renewLock(String lockKey, String lockValue, int timeoutSeconds) {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(timeoutSeconds);
        int result = distributedLockMapper.updateLock(lockKey, lockValue, expireTime);
        return result > 0;
    }
} 