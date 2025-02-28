package com.campus.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.time.LocalDateTime;

@Data
@Alias("productAttribute")
public class ProductAttribute {
    
    private Long id;
    
    private Long productId;
    
    private String name;
    
    private String value;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 