package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.entity.ProductReview;
import com.campus.api.dto.product.ProductReviewQuery;
import com.campus.api.dto.product.ProductReviewDTO;

import java.util.List;
import java.util.Map;

public interface ProductReviewService {
    /**
     * 添加商品评价
     */
    void addReview(ProductReviewDTO reviewDTO);

    /**
     * 获取商品评价列表
     */
    PageResult<ProductReviewDTO> getProductReviews(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 获取用户评价列表
     */
    PageResult<ProductReviewDTO> getUserReviews(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 检查是否已评价
     */
    boolean hasReviewed(Long orderId, Long skuId);

    /**
     * 获取评价列表（后台）
     */
    PageResult<ProductReview> getReviewList(ProductReviewQuery query);

    /**
     * 获取评价详情
     */
    ProductReview getReviewDetail(Long id);

    /**
     * 回复评价
     */
    void replyReview(Long id, String replyContent);

    /**
     * 修改回复
     */
    void updateReply(Long id, String replyContent);

    /**
     * 删除评价
     */
    void deleteReview(Long id);

    /**
     * 更新评价状态
     */
    void updateReviewStatus(Long id, Integer status);

    /**
     * 获取商品评分统计
     */
    Map<String, Object> getProductRatingStats(Long productId);

    /**
     * 批量更新评价状态
     */
    void batchUpdateReviewStatus(List<Long> ids, Integer status);

    /**
     * 批量删除评价
     */
    void batchDeleteReviews(List<Long> ids);
} 