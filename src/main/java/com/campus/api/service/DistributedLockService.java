package com.campus.api.service;

public interface DistributedLockService {
    /**
     * 尝试获取锁
     *
     * @param lockKey 锁键
     * @param timeoutSeconds 超时时间（秒）
     * @return 锁值，如果获取失败返回null
     */
    String tryLock(String lockKey, int timeoutSeconds);

    /**
     * 释放锁
     *
     * @param lockKey 锁键
     * @param lockValue 锁值
     * @return 是否释放成功
     */
    boolean releaseLock(String lockKey, String lockValue);

    /**
     * 续期锁
     *
     * @param lockKey 锁键
     * @param lockValue 锁值
     * @param timeoutSeconds 超时时间（秒）
     * @return 是否续期成功
     */
    boolean renewLock(String lockKey, String lockValue, int timeoutSeconds);
} 