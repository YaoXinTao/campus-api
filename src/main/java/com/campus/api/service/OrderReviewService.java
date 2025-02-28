package com.campus.api.service;

import com.campus.api.dto.OrderReviewDTO;
import com.campus.api.dto.ReviewListDTO;
import com.campus.api.common.PageResult;

public interface OrderReviewService {
    /**
     * 创建订单评价
     *
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderItemId 订单商品ID
     * @param review 评价信息
     */
    void createReview(Long userId, Long orderId, Long orderItemId, OrderReviewDTO review);

    /**
     * 获取商品评价列表
     *
     * @param productId 商品ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评价列表
     */
    PageResult<ReviewListDTO> getProductReviews(Long productId, int pageNum, int pageSize);

    /**
     * 商家回复评价
     *
     * @param reviewId 评价ID
     * @param replyContent 回复内容
     */
    void replyReview(Long reviewId, String replyContent);

    /**
     * 获取用户评价列表
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 评价列表
     */
    PageResult<ReviewListDTO> getUserReviews(Long userId, int pageNum, int pageSize);
} 