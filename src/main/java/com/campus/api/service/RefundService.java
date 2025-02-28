package com.campus.api.service;

import com.campus.api.dto.RefundApplyDTO;
import com.campus.api.dto.RefundDetailDTO;
import com.campus.api.common.PageResult;

public interface RefundService {
    /**
     * 申请退款
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderItemId 订单商品ID
     * @param refundApply 退款申请信息
     * @return 退款记录ID
     */
    Long applyRefund(Long userId, Long orderId, Long orderItemId, RefundApplyDTO refundApply);

    /**
     * 处理退款申请
     *
     * @param refundId 退款记录ID
     * @param agree 是否同意
     * @param rejectReason 拒绝原因（如果不同意）
     */
    void processRefund(Long refundId, boolean agree, String rejectReason);

    /**
     * 获取退款详情
     *
     * @param refundId 退款记录ID
     * @return 退款详情
     */
    RefundDetailDTO getRefundDetail(Long refundId);

    /**
     * 获取用户退款列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 退款列表
     */
    PageResult<RefundDetailDTO> getUserRefunds(Long userId, int pageNum, int pageSize);

    /**
     * 获取商家退款列表
     *
     * @param status 退款状态
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 退款列表
     */
    PageResult<RefundDetailDTO> getMerchantRefunds(Integer status, int pageNum, int pageSize);
} 