package com.campus.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RefundDetailDTO {
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
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * SKU规格数据
     */
    private String skuSpecData;

    /**
     * 退款类型：1-仅退款 2-退货退款
     */
    private Integer refundType;

    /**
     * 退款原因类型
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
    private Boolean isPartial;

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
     * 凭证图片
     */
    private List<String> evidenceImages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 