package com.campus.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ProductTagDTO {
    
    private Long id;
    
    @NotBlank(message = "标签名称不能为空")
    private String name;
    
    private String icon;
    
    private String color;
    
    private Integer status;
    
    private Integer sortOrder;
} 