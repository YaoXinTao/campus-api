package com.campus.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ProductAttributeDTO {
    
    private Long id;
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotBlank(message = "属性名称不能为空")
    private String name;
    
    @NotBlank(message = "属性值不能为空")
    private String value;
    
    private Integer sortOrder;
} 