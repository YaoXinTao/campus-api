package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
public class ShoppingCart {
    private Long id;
    private Long userId;
    private Long productId;
    private Long skuId;
    private Integer quantity;
    private Integer selected;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 商品信息
    private String productName;
    private String productImage;
    // SKU信息
    private String skuName;
    private String skuImage;
    private String skuSpec;
    private BigDecimal skuPrice;
} 