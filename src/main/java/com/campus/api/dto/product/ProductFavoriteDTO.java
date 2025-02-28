package com.campus.api.dto.product;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductFavoriteDTO {
    private Long id;
    private Long productId;
    private String name;
    private String mainImage;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private LocalDateTime createdAt;
} 