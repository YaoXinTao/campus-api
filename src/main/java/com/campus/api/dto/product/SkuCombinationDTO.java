package com.campus.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "SKU规格组合DTO")
public class SkuCombinationDTO {
    
    @Schema(description = "SKU ID")
    private Long id;
    
    @Schema(description = "商品ID")
    private Long productId;
    
    @Schema(description = "SKU编码")
    private String skuCode;
    
    @Schema(description = "规格数据")
    private List<Map<String, String>> specList;
    
    @Schema(description = "销售价")
    private BigDecimal price;
    
    @Schema(description = "市场价")
    private BigDecimal marketPrice;
    
    @Schema(description = "库存")
    private Integer stock;
    
    @Schema(description = "销量")
    private Integer sales;
    
    @Schema(description = "SKU图片")
    private String imageUrl;
    
    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;
} 