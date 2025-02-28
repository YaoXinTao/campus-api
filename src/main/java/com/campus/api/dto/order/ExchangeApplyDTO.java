package com.campus.api.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ExchangeApplyDTO {
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
     * 新SKU ID
     */
    @NotNull(message = "新规格ID不能为空")
    private Long newSkuId;

    /**
     * 换货原因类型：1-尺码不合适 2-颜色与描述不符 3-款式与描述不符 4-其他
     */
    @NotNull(message = "换货原因类型不能为空")
    private Integer exchangeReasonType;

    /**
     * 换货原因
     */
    @NotEmpty(message = "换货原因不能为空")
    private String exchangeReason;

    /**
     * 凭证图片列表
     */
    private List<String> evidenceImages;

    /**
     * 备注
     */
    private String remark;
} 