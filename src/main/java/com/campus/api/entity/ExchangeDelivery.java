package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeDelivery {
    /**
     * 换货物流ID
     */
    private Long id;

    /**
     * 换货记录ID
     */
    private Long exchangeId;

    /**
     * 换货单号
     */
    private String exchangeNo;

    /**
     * 物流类型：1-退货物流 2-换货物流
     */
    private Integer deliveryType;

    /**
     * 物流公司
     */
    private String deliveryCompany;

    /**
     * 物流单号
     */
    private String deliveryNo;

    /**
     * 寄件人姓名
     */
    private String senderName;

    /**
     * 寄件人电话
     */
    private String senderPhone;

    /**
     * 寄件地址
     */
    private String senderAddress;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人电话
     */
    private String receiverPhone;

    /**
     * 收件地址
     */
    private String receiverAddress;

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
     * 物流跟踪数据
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