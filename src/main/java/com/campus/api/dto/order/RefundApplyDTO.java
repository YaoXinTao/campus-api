package com.campus.api.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RefundApplyDTO {
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单商品ID
     */
    @NotNull(message = "订单商品ID不能为空")
    private Long orderItemId;

    /**
     * 退款类型：1-仅退款 2-退货退款
     */
    @NotNull(message = "退款类型不能为空")
    private Integer refundType;

    /**
     * 退款原因类型：1-质量问题 2-商品与描述不符 3-商品损坏 4-尺寸不合适 5-其他
     */
    @NotNull(message = "退款原因类型不能为空")
    private Integer refundReasonType;

    /**
     * 退款原因
     */
    @NotEmpty(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    private BigDecimal refundAmount;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /**
     * 凭证图片列表
     */
    private List<String> evidenceImages;

    /**
     * 退款说明
     */
    private String remark;
} 