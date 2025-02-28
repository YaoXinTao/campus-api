package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.dto.order.CreateOrderDTO;
import com.campus.api.dto.order.OrderDetailDTO;
import com.campus.api.dto.order.OrderQueryDTO;
import com.campus.api.dto.order.RefundApplyDTO;
import com.campus.api.dto.order.ExchangeApplyDTO;
import com.campus.api.dto.order.ExchangeDeliveryDTO;
import com.campus.api.dto.order.ExchangeDetailDTO;
import com.campus.api.dto.order.TrackingInfo;

import java.util.List;

public interface OrderService {
    /**
     * 创建订单
     *
     * @param userId 用户ID
     * @param createOrderDTO 创建订单请求
     * @return 订单ID
     */
    Long createOrder(Long userId, CreateOrderDTO createOrderDTO);

    /**
     * 获取订单详情
     *
     * @param id 订单ID
     * @return 订单详情
     */
    OrderDetailDTO getOrderDetail(Long id);

    /**
     * 获取订单列表
     *
     * @param queryDTO 查询条件
     * @return 订单列表
     */
    PageResult<OrderDetailDTO> getOrderList(OrderQueryDTO queryDTO);

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param reason 取消原因
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 支付订单
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param paymentMethod 支付方式：1-微信支付 2-支付宝 3-余额支付
     * @return 支付参数（如微信支付参数）
     */
    Object payOrder(Long userId, Long orderId, Integer paymentMethod);

    /**
     * 确认收货
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void confirmReceive(Long userId, Long orderId);

    /**
     * 申请退款
     *
     * @param userId 用户ID
     * @param refundApplyDTO 退款申请
     */
    void applyRefund(Long userId, RefundApplyDTO refundApplyDTO);

    /**
     * 发货
     *
     * @param orderId 订单ID
     * @param deliveryCompany 物流公司
     * @param deliveryNo 物流单号
     */
    void deliver(Long orderId, String deliveryCompany, String deliveryNo);

    /**
     * 同意退款
     *
     * @param refundId 退款记录ID
     */
    void agreeRefund(Long refundId);

    /**
     * 拒绝退款
     *
     * @param refundId 退款记录ID
     * @param reason 拒绝原因
     */
    void rejectRefund(Long refundId, String reason);

    /**
     * 删除订单（仅支持已完成或已取消的订单）
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     */
    void deleteOrder(Long userId, Long orderId);

    /**
     * 申请换货
     *
     * @param userId 用户ID
     * @param exchangeApplyDTO 换货申请
     */
    void applyExchange(Long userId, ExchangeApplyDTO exchangeApplyDTO);

    /**
     * 同意换货申请
     *
     * @param exchangeId 换货记录ID
     */
    void agreeExchange(Long exchangeId);

    /**
     * 拒绝换货申请
     *
     * @param exchangeId 换货记录ID
     * @param reason 拒绝原因
     */
    void rejectExchange(Long exchangeId, String reason);

    /**
     * 买家退回商品（填写退货物流）
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     * @param deliveryDTO 物流信息
     */
    void returnGoods(Long userId, Long exchangeId, ExchangeDeliveryDTO deliveryDTO);

    /**
     * 确认收到退回商品
     *
     * @param exchangeId 换货记录ID
     */
    void confirmReturn(Long exchangeId);

    /**
     * 发送换货商品
     *
     * @param exchangeId 换货记录ID
     * @param deliveryCompany 物流公司
     * @param deliveryNo 物流单号
     */
    void sendExchangeGoods(Long exchangeId, String deliveryCompany, String deliveryNo);

    /**
     * 确认收到换货商品
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     */
    void confirmExchangeReceive(Long userId, Long exchangeId);

    /**
     * 取消换货申请
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     */
    void cancelExchange(Long userId, Long exchangeId);

    /**
     * 更新换货收货地址
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     * @param addressId 新的收货地址ID
     */
    void updateExchangeAddress(Long userId, Long exchangeId, Long addressId);

    /**
     * 获取换货详情
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     * @return 换货详情
     */
    ExchangeDetailDTO getExchangeDetail(Long userId, Long exchangeId);

    /**
     * 获取换货列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param exchangeStatus 换货状态
     * @return 换货列表
     */
    PageResult<ExchangeDetailDTO> getExchangeList(Long userId, Integer pageNum, Integer pageSize, Integer exchangeStatus);

    /**
     * 管理员获取换货列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param exchangeStatus 换货状态
     * @param userId 用户ID
     * @param orderNo 订单编号
     * @param exchangeNo 换货单号
     * @return 换货列表
     */
    PageResult<ExchangeDetailDTO> getExchangeListForAdmin(Integer pageNum, Integer pageSize, Integer exchangeStatus,
                                                         Long userId, String orderNo, String exchangeNo);

    /**
     * 管理员获取换货详情
     *
     * @param exchangeId 换货记录ID
     * @return 换货详情
     */
    ExchangeDetailDTO getExchangeDetailForAdmin(Long exchangeId);

    /**
     * 获取换货物流跟踪信息
     *
     * @param userId 用户ID
     * @param exchangeId 换货记录ID
     * @param type 物流类型：1-退货物流 2-换货物流，不传则返回所有
     * @return 物流跟踪信息列表
     */
    List<TrackingInfo> getExchangeDeliveryTracking(Long userId, Long exchangeId, Integer type);
} 