package com.campus.api.dto.stats;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HotProductDTO {
    // 商品ID
    private Long id;
    // 商品名称
    private String name;
    // 商品简介
    private String brief;
    // 主图
    private String mainImage;
    // 分类名称
    private String categoryName;
    // 售价
    private BigDecimal price;
    // 销量
    private Integer sales;
    // 评分
    private Double rating;
} 