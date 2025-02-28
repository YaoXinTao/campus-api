package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDetail {
    /**
     * 详情ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 富文本详情
     */
    private String richContent;

    /**
     * 规格参数描述
     */
    private String specDesc;

    /**
     * 包装清单
     */
    private String packingList;

    /**
     * 售后服务
     */
    private String serviceNotes;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 