package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private Long orderId;
    private Integer status;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /**
     * 版本号，用于乐观锁
     */
    private Integer version;
} 