package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductViewLog {
    /**
     * 记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 