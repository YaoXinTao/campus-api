package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货人电话
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付方式：1-微信支付 2-支付宝
     */
    private Integer paymentMethod;

    /**
     * 订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款
     */
    private Integer orderStatus;

    /**
     * 支付状态：0-未支付 1-已支付 2-已退款
     */
    private Integer paymentStatus;

    /**
     * 发货状态：0-未发货 1-已发货 2-已收货
     */
    private Integer deliveryStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 