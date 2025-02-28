package com.campus.api.dto.product;

import lombok.Data;

@Data
public class ProductReviewQuery {
    /**
     * 搜索关键字（商品名称/用户名）
     */
    private String keyword;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 状态（0-隐藏 1-显示）
     */
    private Integer status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
} 