package com.campus.api.dto.stats;

import lombok.Data;
import java.util.List;

@Data
public class ProductSalesTrendDTO {
    // 日期列表
    private List<String> dates;
    // 销量数据
    private List<Integer> values;
} 