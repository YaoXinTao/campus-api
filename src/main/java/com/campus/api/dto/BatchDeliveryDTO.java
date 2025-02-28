package com.campus.api.dto;

import lombok.Data;

@Data
public class BatchDeliveryDTO {
    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 物流公司
     */
    private String deliveryCompany;

    /**
     * 物流单号
     */
    private String deliveryNo;
} 