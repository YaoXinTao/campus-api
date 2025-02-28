package com.campus.api.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RefundApplyDTO {
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
     * 凭证图片
     */
    private List<String> evidenceImages;

    /**
     * 退款说明
     */
    private String remark;
} 