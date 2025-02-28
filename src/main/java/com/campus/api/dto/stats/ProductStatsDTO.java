package com.campus.api.dto.stats;

import lombok.Data;
import java.util.List;

@Data
public class ProductStatsDTO {
    // 数据概览
    private ProductStatsOverviewDTO overview;
    // 销量趋势
    private ProductSalesTrendDTO salesTrend;
    // 分类统计
    private List<CategoryStatsDTO> categoryStats;
    // 评分分布
    private List<Integer> ratingStats;
    // 热销商品
    private List<HotProductDTO> hotProducts;
} 