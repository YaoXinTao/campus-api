package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
    /**
     * 详情ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
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
     * SKU规格数据（JSON）
     */
    private String skuSpecData;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 已退款数量
     */
    private Integer refundedQuantity;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态：0-无退款 1-退款中 2-已退款 3-退款失败
     */
    private Integer refundStatus;

    /**
     * 退款记录ID
     */
    private Long refundId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 