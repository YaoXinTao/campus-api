package com.campus.api.dto.stats;

import lombok.Data;

@Data
public class CategoryStatsDTO {
    // 分类名称
    private String name;
    // 商品数量
    private Integer value;
} 