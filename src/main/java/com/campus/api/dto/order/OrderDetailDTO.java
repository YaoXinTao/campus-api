package com.campus.api.dto.order;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailDTO {
    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 运费金额
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 使用的优惠券ID
     */
    private Long couponId;

    /**
     * 收货人姓名
     */
    private String receiver;

    /**
     * 收货人手机号
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款
     */
    private Integer orderStatus;

    /**
     * 支付状态：0-未支付 1-已支付 2-已退款
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 发货状态：0-未发货 1-已发货 2-已收货
     */
    private Integer deliveryStatus;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 订单商品列表
     */
    private List<OrderItemDTO> items;

    /**
     * 物流信息
     */
    private OrderDeliveryDTO delivery;

    @Data
    public static class OrderItemDTO {
        /**
         * 商品ID
         */
        private Long productId;

        /**
         * SKU ID
         */
        private Long skuId;

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
        private Object skuSpecData;

        /**
         * 商品单价
         */
        private BigDecimal price;

        /**
         * 购买数量
         */
        private Integer quantity;

        /**
         * 总金额
         */
        private BigDecimal totalAmount;

        /**
         * 退款状态：0-无退款 1-退款中 2-已退款 3-退款失败
         */
        private Integer refundStatus;

        /**
         * 退款记录ID
         */
        private Long refundId;
    }

    @Data
    public static class OrderDeliveryDTO {
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
         * 物流跟踪数据
         */
        private Object trackingData;
    }
} 