package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品简介
     */
    private String brief;

    /**
     * 商品关键字
     */
    private String keywords;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 商品相册(JSON格式)
     */
    private String album;

    /**
     * 商品单位
     */
    private String unit;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 总销量
     */
    private Integer totalSales;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;

    /**
     * 审核状态：0-未审核 1-审核通过 2-审核不通过
     */
    private Integer verifyStatus;

    /**
     * 是否推荐：0-否 1-是
     */
    private Integer isFeatured;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 