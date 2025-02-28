package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentRecord {
    /**
     * 支付记录ID
     */
    private Long id;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付方式：1-微信支付 2-支付宝 3-余额支付
     */
    private Integer paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 第三方支付交易号
     */
    private String transactionId;

    /**
     * 支付状态：0-待支付 1-支付成功 2-支付失败
     */
    private Integer paymentStatus;

    /**
     * 支付成功时间
     */
    private LocalDateTime paymentTime;

    /**
     * 回调时间
     */
    private LocalDateTime callbackTime;

    /**
     * 回调内容
     */
    private String callbackContent;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 