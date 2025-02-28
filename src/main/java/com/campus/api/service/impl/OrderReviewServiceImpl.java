package com.campus.api.service.impl;

import com.campus.api.service.OrderReviewService;
import com.campus.api.mapper.OrderReviewMapper;
import com.campus.api.mapper.OrderMapper;
import com.campus.api.dto.OrderReviewDTO;
import com.campus.api.dto.ReviewListDTO;
import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderReviewServiceImpl implements OrderReviewService {

    @Autowired
    private OrderReviewMapper orderReviewMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createReview(Long userId, Long orderId, Long orderItemId, OrderReviewDTO review) {
        // 检查订单是否存在且属于当前用户
        if (!orderMapper.checkOrderBelongsToUser(orderId, userId)) {
            throw new BusinessException("订单不存在或不属于当前用户");
        }

        // 检查订单商品是否已评价
        if (orderReviewMapper.checkReviewExists(orderId, orderItemId)) {
            throw new BusinessException("该商品已评价");
        }

        // 创建评价
        orderReviewMapper.insertReview(userId, orderId, orderItemId, review);

        // 更新订单商品评价状态
        orderMapper.updateOrderItemReviewStatus(orderItemId);

        // 检查订单是否所有商品都已评价，如果是则更新订单评价状态
        if (orderMapper.checkAllItemsReviewed(orderId)) {
            orderMapper.updateOrderReviewStatus(orderId);
        }
    }

    @Override
    public PageResult<ReviewListDTO> getProductReviews(Long productId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ReviewListDTO> list = orderReviewMapper.selectProductReviews(productId);
        long total = list.size();
        
        return new PageResult<ReviewListDTO>(total, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long reviewId, String replyContent) {
        orderReviewMapper.updateReviewReply(reviewId, replyContent, LocalDateTime.now());
    }

    @Override
    public PageResult<ReviewListDTO> getUserReviews(Long userId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ReviewListDTO> list = orderReviewMapper.selectUserReviews(userId);
        long total = list.size();
        
        return new PageResult<ReviewListDTO>(total, list);
    }
} 