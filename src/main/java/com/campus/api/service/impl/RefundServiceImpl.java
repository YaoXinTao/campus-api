package com.campus.api.service.impl;

import com.campus.api.service.RefundService;
import com.campus.api.service.BusinessRuleService;
import com.campus.api.mapper.RefundMapper;
import com.campus.api.mapper.OrderMapper;
import com.campus.api.dto.RefundApplyDTO;
import com.campus.api.dto.RefundDetailDTO;
import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.common.util.SnowflakeIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RefundServiceImpl implements RefundService {

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private BusinessRuleService businessRuleService;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyRefund(Long userId, Long orderId, Long orderItemId, RefundApplyDTO refundApply) {
        log.info("用户申请退款，用户ID：{}，订单ID：{}，商品ID：{}", userId, orderId, orderItemId);
        try {
            // 检查订单是否存在且属于当前用户
            if (!orderMapper.checkOrderBelongsToUser(orderId, userId)) {
                log.warn("订单不存在或不属于当前用户，用户ID：{}，订单ID：{}", userId, orderId);
                throw new BusinessException("订单不存在或不属于当前用户");
            }

            // 检查订单商品是否可以退款
            if (!orderMapper.checkOrderItemCanRefund(orderItemId)) {
                log.warn("该商品不可退款，订单ID：{}，商品ID：{}", orderId, orderItemId);
                throw new BusinessException("该商品不可退款");
            }

            // 检查退款次数是否超过限制
            int maxRefundTimes = businessRuleService.getRuleValueAsInt("REFUND", "MAX_TIMES");
            if (refundMapper.countRefundTimes(orderId) >= maxRefundTimes) {
                log.warn("退款次数已达上限，订单ID：{}", orderId);
                throw new BusinessException("退款次数已达上限");
            }

            // 检查退款金额是否合理
            if (!orderMapper.checkRefundAmountValid(orderItemId, refundApply.getRefundAmount(), refundApply.getRefundQuantity())) {
                log.warn("退款金额不合理，订单ID：{}，商品ID：{}", orderId, orderItemId);
                throw new BusinessException("退款金额不合理");
            }

            // 生成退款单号
            String refundNo = "R" + idGenerator.nextId();
            log.info("生成退款单号：{}", refundNo);

            // 创建退款记录
            refundMapper.insertRefund(refundNo, userId, orderId, orderItemId, refundApply);

            // 如果是部分退款，创建退款商品记录
            if (refundApply.getRefundQuantity() < orderMapper.getOrderItemQuantity(orderItemId)) {
                refundMapper.insertRefundItem(refundNo, orderItemId, refundApply.getRefundQuantity(), refundApply.getRefundAmount());
            }

            // 更新订单商品退款状态
            orderMapper.updateOrderItemRefundStatus(orderItemId, 1); // 1-退款中

            Long refundId = refundMapper.getRefundIdByNo(refundNo);
            log.info("退款申请创建成功，退款ID：{}", refundId);
            return refundId;
        } catch (Exception e) {
            log.error("退款申请失败，用户ID：{}，订单ID：{}", userId, orderId, e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processRefund(Long refundId, boolean agree, String rejectReason) {
        log.info("处理退款申请，退款ID：{}，是否同意：{}，拒绝原因：{}", refundId, agree, rejectReason);
        try {
            RefundDetailDTO refund = refundMapper.getRefundDetail(refundId);
            if (refund == null) {
                log.warn("退款记录不存在，退款ID：{}", refundId);
                throw new BusinessException("退款记录不存在");
            }

            if (refund.getRefundStatus() != 0) {
                log.warn("退款状态不正确，退款ID：{}，当前状态：{}", refundId, refund.getRefundStatus());
                throw new BusinessException("退款状态不正确");
            }

            if (agree) {
                // 同意退款
                refundMapper.updateRefundStatus(refundId, 3, null, null); // 3-已完成
                refundMapper.updateRefundTime(refundId, LocalDateTime.now());

                // 更新订单商品退款状态和退款数量
                orderMapper.updateOrderItemRefundStatus(refund.getOrderItemId(), 2); // 2-已退款
                orderMapper.updateOrderItemRefundedQuantity(refund.getOrderItemId(), refund.getRefundQuantity());

                // 如果订单所有商品都已退款，更新订单状态
                if (orderMapper.checkAllItemsRefunded(refund.getOrderId())) {
                    orderMapper.updateOrderStatus(refund.getOrderId(), 60); // 60-已退款
                }
                log.info("退款申请已同意，退款ID：{}", refundId);
            } else {
                // 拒绝退款
                refundMapper.updateRefundStatus(refundId, 2, rejectReason, LocalDateTime.now()); // 2-已拒绝
                orderMapper.updateOrderItemRefundStatus(refund.getOrderItemId(), 3); // 3-退款失败
                log.info("退款申请已拒绝，退款ID：{}", refundId);
            }
        } catch (Exception e) {
            log.error("处理退款申请失败，退款ID：{}", refundId, e);
            throw e;
        }
    }

    @Override
    public RefundDetailDTO getRefundDetail(Long refundId) {
        log.info("获取退款详情，退款ID：{}", refundId);
        try {
            RefundDetailDTO detail = refundMapper.getRefundDetail(refundId);
            if (detail == null) {
                log.warn("退款记录不存在，退款ID：{}", refundId);
                throw new BusinessException("退款记录不存在");
            }
            return detail;
        } catch (Exception e) {
            log.error("获取退款详情失败，退款ID：{}", refundId, e);
            throw e;
        }
    }

    @Override
    public PageResult<RefundDetailDTO> getUserRefunds(Long userId, int pageNum, int pageSize) {
        log.info("获取用户退款列表，用户ID：{}，页码：{}，每页数量：{}", userId, pageNum, pageSize);
        try {
            List<RefundDetailDTO> list = refundMapper.selectUserRefunds(userId);
            long total = list.size();
            return new PageResult<>(total, list);
        } catch (Exception e) {
            log.error("获取用户退款列表失败，用户ID：{}", userId, e);
            throw e;
        }
    }

    @Override
    public PageResult<RefundDetailDTO> getMerchantRefunds(Integer status, int pageNum, int pageSize) {
        log.info("获取商家退款列表，状态：{}，页码：{}，每页数量：{}", status, pageNum, pageSize);
        try {
            List<RefundDetailDTO> list = refundMapper.selectMerchantRefunds(status);
            long total = list.size();
            return new PageResult<>(total, list);
        } catch (Exception e) {
            log.error("获取商家退款列表失败", e);
            throw e;
        }
    }
} 