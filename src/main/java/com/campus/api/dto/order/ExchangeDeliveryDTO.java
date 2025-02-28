package com.campus.api.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExchangeDeliveryDTO {
    /**
     * 换货记录ID
     */
    @NotNull(message = "换货记录ID不能为空")
    private Long exchangeId;

    /**
     * 物流公司
     */
    @NotEmpty(message = "物流公司不能为空")
    private String deliveryCompany;

    /**
     * 物流单号
     */
    @NotEmpty(message = "物流单号不能为空")
    private String deliveryNo;

    /**
     * 寄件人姓名
     */
    @NotEmpty(message = "寄件人姓名不能为空")
    private String senderName;

    /**
     * 寄件人电话
     */
    @NotEmpty(message = "寄件人电话不能为空")
    private String senderPhone;

    /**
     * 寄件地址
     */
    @NotEmpty(message = "寄件地址不能为空")
    private String senderAddress;
} 