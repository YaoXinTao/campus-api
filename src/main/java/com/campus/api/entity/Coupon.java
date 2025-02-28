package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;
    private String name;
    private Integer type;
    private BigDecimal amount;
    private BigDecimal minSpend;
    private Long categoryId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalCount;
    private Integer remainCount;
    private Integer perLimit;
    private String description;
    private Integer status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 