package com.campus.api.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExchangeItemDTO {
    /**
     * 换货商品ID
     */
    private Long id;

    /**
     * 换货ID
     */
    private Long exchangeId;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品SKU ID
     */
    private Long skuId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 商品规格
     */
    private String specifications;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 换货数量
     */
    private Integer quantity;
} 