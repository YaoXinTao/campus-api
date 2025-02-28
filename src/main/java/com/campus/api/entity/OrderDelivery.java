package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderDelivery {
    /**
     * 订单物流ID
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
     * 物流公司
     */
    private String deliveryCompany;

    /**
     * 物流单号
     */
    private String deliveryNo;

    /**
     * 物流状态：0-待发货 1-已发货 2-已签收
     */
    private Integer deliveryStatus;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 签收时间
     */
    private LocalDateTime receiveTime;

    /**
     * 物流跟踪数据（JSON）
     */
    private String trackingData;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 