package com.campus.api.dto;

import lombok.Data;

@Data
public class ProductSearchDTO {
    
    private String keyword;
    
    private Long categoryId;
    
    private Long tagId;
    
    private String sortField;
    
    private String sortOrder;
    
    private Integer page = 1;
    
    private Integer size = 10;
    
    private Integer minPrice;
    
    private Integer maxPrice;
    
    private Boolean onlyInStock;
    
    private Boolean featured;
} 