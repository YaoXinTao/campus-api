package com.campus.api.service;

/**
 * 消息服务接口
 */
public interface MessageService {
    /**
     * 发送订单创建消息
     *
     * @param orderId 订单ID
     */
    void sendOrderCreatedMessage(Long orderId);

    /**
     * 发送订单支付成功消息
     *
     * @param orderId 订单ID
     */
    void sendOrderPaidMessage(Long orderId);

    /**
     * 发送订单发货消息
     *
     * @param orderId 订单ID
     */
    void sendOrderShippedMessage(Long orderId);

    /**
     * 发送订单完成消息
     *
     * @param orderId 订单ID
     */
    void sendOrderCompletedMessage(Long orderId);

    /**
     * 发送订单取消消息
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     */
    void sendOrderCancelledMessage(Long orderId, String reason);

    /**
     * 发送退款申请消息
     *
     * @param orderId 订单ID
     */
    void sendRefundAppliedMessage(Long orderId);

    /**
     * 发送退款成功消息
     *
     * @param orderId 订单ID
     */
    void sendRefundSuccessMessage(Long orderId);

    /**
     * 发送退款成功通知
     *
     * @param refundId 退款ID
     */
    void sendRefundSuccessNotice(Long refundId);

    /**
     * 发送换货申请消息
     *
     * @param exchangeId 换货ID
     */
    void sendExchangeAppliedMessage(Long exchangeId);

    /**
     * 发送换货审核通过消息
     *
     * @param exchangeId 换货ID
     */
    void sendExchangeApprovedMessage(Long exchangeId);

    /**
     * 发送换货审核拒绝消息
     *
     * @param exchangeId 换货ID
     * @param reason     拒绝原因
     */
    void sendExchangeRejectedMessage(Long exchangeId, String reason);

    /**
     * 发送换货完成消息
     *
     * @param exchangeId 换货ID
     */
    void sendExchangeCompletedMessage(Long exchangeId);

    /**
     * 发送换货申请通知给商家
     */
    void sendExchangeApplyNotice(Long exchangeId);

    /**
     * 发送换货申请结果通知给用户
     */
    void sendExchangeResultNotice(Long exchangeId, boolean agreed, String reason);

    /**
     * 发送退货提醒给用户
     */
    void sendReturnGoodsReminder(Long refundId);

    /**
     * 发送换货商品发货通知给用户
     */
    void sendExchangeDeliveryNotice(Long exchangeId, String deliveryCompany, String deliveryNo);

    /**
     * 发送换货完成通知
     */
    void sendExchangeCompleteNotice(Long exchangeId);

    /**
     * 发送换货超时取消通知
     */
    void sendExchangeTimeoutNotice(Long exchangeId, String reason);

    /**
     * 发送优惠券退回通知
     *
     * @param userId    用户ID
     * @param couponName 优惠券名称
     * @param orderNo    订单号
     */
    void sendCouponReturnNotice(Long userId, String couponName, String orderNo);
} 