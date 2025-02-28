package com.campus.api.dto.order;

import com.campus.api.entity.ExchangeDelivery;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExchangeDetailDTO {
    /**
     * 换货ID
     */
    private Long id;

    /**
     * 换货单号
     */
    private String exchangeNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 原SKU信息
     */
    private SkuInfo oldSku;

    /**
     * 新SKU信息
     */
    private SkuInfo newSku;

    /**
     * 换货状态：
     * 1-待审核
     * 2-审核通过
     * 3-审核拒绝
     * 4-待退货
     * 5-退货中
     * 6-待换货
     * 7-换货中
     * 8-已完成
     */
    private Integer exchangeStatus;

    /**
     * 换货原因
     */
    private String reason;

    /**
     * 换货说明
     */
    private String description;

    /**
     * 凭证图片
     */
    private List<String> evidenceImages;

    /**
     * 退货物流公司
     */
    private String returnCompany;

    /**
     * 退货物流单号
     */
    private String returnTrackingNo;

    /**
     * 换货物流公司
     */
    private String exchangeCompany;

    /**
     * 换货物流单号
     */
    private String exchangeTrackingNo;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverPhone;

    /**
     * 收货地址
     */
    private String receiverAddress;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 退货时间
     */
    private LocalDateTime returnTime;

    /**
     * 换货时间
     */
    private LocalDateTime exchangeTime;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 换货商品信息
     */
    private List<ExchangeItemDTO> items;

    private DeliveryInfo returnDelivery;
    private DeliveryInfo exchangeDelivery;
    
    public void setReturnDelivery(ExchangeDelivery delivery) {
        if (delivery == null) {
            return;
        }
        DeliveryInfo info = new DeliveryInfo();
        BeanUtils.copyProperties(delivery, info);
        this.returnDelivery = info;
    }
    
    public void setExchangeDelivery(ExchangeDelivery delivery) {
        if (delivery == null) {
            return;
        }
        DeliveryInfo info = new DeliveryInfo();
        BeanUtils.copyProperties(delivery, info);
        this.exchangeDelivery = info;
    }

    @Data
    public static class SkuInfo {
        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * 商品ID
         */
        private Long productId;

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
    }

    @Data
    public static class DeliveryInfo {
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
        private Object trackingData;
    }
} 