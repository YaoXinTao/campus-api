package com.campus.api.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundRecord {
    /**
     * 退款记录ID
     */
    private Long id;

    /**
     * 退款单号
     */
    private String refundNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单详情ID
     */
    private Long orderItemId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 退款类型：1-仅退款 2-退货退款
     */
    private Integer refundType;

    /**
     * 退款原因类型：1-质量问题 2-商品与描述不符 3-商品损坏 4-尺寸不合适 5-其他
     */
    private Integer refundReasonType;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款数量
     */
    private Integer refundQuantity;

    /**
     * 是否部分退款
     */
    private Integer isPartial;

    /**
     * 退款状态：0-待处理 1-已同意 2-已拒绝 3-已完成
     */
    private Integer refundStatus;

    /**
     * 退款完成时间
     */
    private LocalDateTime refundTime;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 拒绝时间
     */
    private LocalDateTime rejectTime;

    /**
     * 凭证图片（JSON数组）
     */
    private String evidenceImages;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 管理员备注
     */
    private String adminNote;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 自动批准
     */
    private Integer autoApproved;

    /**
     * 备注
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