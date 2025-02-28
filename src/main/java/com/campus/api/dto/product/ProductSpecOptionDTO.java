package com.campus.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "商品规格选项DTO")
public class ProductSpecOptionDTO {
    
    @Schema(description = "规格名称")
    private String name;
    
    @Schema(description = "规格值列表")
    private List<String> values;
} 