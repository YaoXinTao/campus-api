package com.campus.api.dto.order;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderQueryDTO {
    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款
     */
    private Integer orderStatus;

    /**
     * 支付状态：0-未支付 1-已支付 2-已退款
     */
    private Integer paymentStatus;

    /**
     * 发货状态：0-未发货 1-已发货 2-已收货
     */
    private Integer deliveryStatus;

    /**
     * 收货人姓名
     */
    private String receiver;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
} 