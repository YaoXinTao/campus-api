package com.campus.api.service;

import com.campus.api.dto.stats.ProductStatsDTO;

public interface ProductStatsService {
    /**
     * 获取商品统计数据
     * @param type 统计类型：week-近7天 month-近30天
     * @return 商品统计数据
     */
    ProductStatsDTO getProductStats(String type);
} 