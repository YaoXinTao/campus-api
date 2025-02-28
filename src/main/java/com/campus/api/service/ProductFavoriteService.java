package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.dto.product.ProductFavoriteDTO;

public interface ProductFavoriteService {
    /**
     * 添加收藏
     */
    void addFavorite(Long userId, Long productId);

    /**
     * 取消收藏
     */
    void removeFavorite(Long userId, Long productId);

    /**
     * 获取用户收藏列表
     */
    PageResult<ProductFavoriteDTO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long productId);
} 