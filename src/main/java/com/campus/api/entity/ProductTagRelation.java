package com.campus.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;
import java.time.LocalDateTime;

@Data
@Alias("productTagRelation")
public class ProductTagRelation {
    
    private Long id;
    
    private Long productId;
    
    private Long tagId;
    
    private LocalDateTime createdAt;
} 