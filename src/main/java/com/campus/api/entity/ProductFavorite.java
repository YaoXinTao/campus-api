package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductFavorite {
    /**
     * 收藏ID
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