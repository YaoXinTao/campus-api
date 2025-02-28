package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductTag {
    private Long id;
    private String name;
    private String icon;
    private String color;
    private Integer status;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 