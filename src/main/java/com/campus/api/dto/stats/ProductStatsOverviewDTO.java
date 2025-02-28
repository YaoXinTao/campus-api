package com.campus.api.dto.stats;

import lombok.Data;

@Data
public class ProductStatsOverviewDTO {
    // 总商品数
    private Integer totalProducts;
    // 商品增长数
    private Integer productIncrease;
    // 总销量
    private Integer totalSales;
    // 销量增长数
    private Integer salesIncrease;
    // 总评价数
    private Integer totalComments;
    // 评价增长数
    private Integer commentIncrease;
    // 平均评分
    private Double averageRating;
    // 评分增长值
    private Double ratingIncrease;
} 