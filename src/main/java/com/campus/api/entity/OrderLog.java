package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderLog {
    /**
     * 日志ID
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
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人类型：1-用户 2-管理员
     */
    private Integer operatorType;

    /**
     * 操作类型：1-创建订单 2-支付订单 3-发货 4-确认收货 5-取消订单 6-申请退款 7-同意退款 8-拒绝退款 9-退款完成
     */
    private Integer actionType;

    /**
     * 操作描述
     */
    private String actionDesc;

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 