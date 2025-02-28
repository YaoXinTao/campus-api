package com.campus.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import java.time.LocalDateTime;

@Mapper
public interface DistributedLockMapper {
    
    @Insert("INSERT INTO distributed_locks (lock_key, lock_value, expire_time) " +
            "SELECT #{lockKey}, #{lockValue}, #{expireTime} " +
            "WHERE NOT EXISTS (SELECT 1 FROM distributed_locks WHERE lock_key = #{lockKey} AND expire_time > NOW())")
    int insertLock(@Param("lockKey") String lockKey, @Param("lockValue") String lockValue, @Param("expireTime") LocalDateTime expireTime);
    
    @Update("UPDATE distributed_locks SET lock_value = #{lockValue}, expire_time = #{expireTime}, updated_at = NOW() " +
            "WHERE lock_key = #{lockKey} AND expire_time < NOW()")
    int updateExpiredLock(@Param("lockKey") String lockKey, @Param("lockValue") String lockValue, @Param("expireTime") LocalDateTime expireTime);
    
    @Update("UPDATE distributed_locks SET expire_time = #{expireTime}, updated_at = NOW() " +
            "WHERE lock_key = #{lockKey} AND lock_value = #{lockValue}")
    int updateLock(@Param("lockKey") String lockKey, @Param("lockValue") String lockValue, @Param("expireTime") LocalDateTime expireTime);
    
    @Delete("DELETE FROM distributed_locks WHERE lock_key = #{lockKey} AND lock_value = #{lockValue}")
    int deleteLock(@Param("lockKey") String lockKey, @Param("lockValue") String lockValue);
    
    @Delete("DELETE FROM distributed_locks WHERE expire_time < NOW()")
    int deleteExpiredLocks();
} 