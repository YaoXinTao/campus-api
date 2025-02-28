package com.campus.api.service.impl;

import com.campus.api.entity.ExchangeRecord;
import com.campus.api.entity.Order;
import com.campus.api.mapper.ExchangeRecordMapper;
import com.campus.api.mapper.OrderMapper;
import com.campus.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ExchangeRecordMapper exchangeRecordMapper;
    private final OrderMapper orderMapper;

    @Override
    public void sendOrderCreatedMessage(Long orderId) {
        log.info("发送订单创建消息，orderId: {}", orderId);
    }

    @Override
    public void sendOrderPaidMessage(Long orderId) {
        log.info("发送订单支付成功消息，orderId: {}", orderId);
    }

    @Override
    public void sendOrderShippedMessage(Long orderId) {
        log.info("发送订单发货消息，orderId: {}", orderId);
    }

    @Override
    public void sendOrderCompletedMessage(Long orderId) {
        log.info("发送订单完成消息，orderId: {}", orderId);
    }

    @Override
    public void sendOrderCancelledMessage(Long orderId, String reason) {
        log.info("发送订单取消消息，orderId: {}，reason: {}", orderId, reason);
    }

    @Override
    public void sendRefundAppliedMessage(Long orderId) {
        log.info("发送退款申请消息，orderId: {}", orderId);
    }

    @Override
    public void sendRefundSuccessMessage(Long orderId) {
        log.info("发送退款成功消息，orderId: {}", orderId);
    }

    @Override
    public void sendRefundSuccessNotice(Long orderId) {
        log.info("发送退款成功通知，orderId: {}", orderId);
    }

    @Override
    public void sendExchangeAppliedMessage(Long exchangeId) {
        log.info("发送换货申请消息，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeApprovedMessage(Long exchangeId) {
        log.info("发送换货审核通过消息，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeRejectedMessage(Long exchangeId, String reason) {
        log.info("发送换货审核拒绝消息，exchangeId: {}，reason: {}", exchangeId, reason);
    }

    @Override
    public void sendExchangeCompletedMessage(Long exchangeId) {
        log.info("发送换货完成消息，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeApplyNotice(Long exchangeId) {
        log.info("发送换货申请通知给商家，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeResultNotice(Long exchangeId, boolean isAgreed, String reason) {
        log.info("发送换货申请结果通知给用户，exchangeId: {}，isAgreed: {}，reason: {}", exchangeId, isAgreed, reason);
    }

    @Override
    public void sendReturnGoodsReminder(Long exchangeId) {
        log.info("发送退货提醒给用户，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeDeliveryNotice(Long exchangeId, String deliveryCompany, String deliveryNo) {
        log.info("发送换货商品发货通知给用户，exchangeId: {}，deliveryCompany: {}，deliveryNo: {}", 
                exchangeId, deliveryCompany, deliveryNo);
    }

    @Override
    public void sendExchangeCompleteNotice(Long exchangeId) {
        log.info("发送换货完成通知，exchangeId: {}", exchangeId);
    }

    @Override
    public void sendExchangeTimeoutNotice(Long exchangeId, String reason) {
        log.info("发送换货超时取消通知，exchangeId: {}，reason: {}", exchangeId, reason);
    }

    @Override
    public void sendCouponReturnNotice(Long userId, String couponName, String orderNo) {
        try {
            // 构建消息内容
            String content = String.format("您的订单 %s 已取消/退款，优惠券\"%s\"已退回您的账户，可以继续使用。", orderNo, couponName);
            
            // TODO: 根据实际的消息发送方式实现
            // 可以是发送微信模板消息、系统内部消息、短信等
            log.info("发送优惠券退回通知：userId={}, content={}", userId, content);
            
        } catch (Exception e) {
            log.error("发送优惠券退回通知失败：userId={}, couponName={}, orderNo={}", userId, couponName, orderNo, e);
        }
    }
} 