package com.campus.api.dto.product;

import lombok.Data;

@Data
public class ProductQuery {
    /**
     * 搜索关键字
     */
    private String keyword;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 审核状态
     */
    private Integer verifyStatus;

    /**
     * 是否推荐
     */
    private Integer isFeatured;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式：asc/desc
     */
    private String sortOrder;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    // 价格区间
    private Integer minPrice;
    private Integer maxPrice;
} 