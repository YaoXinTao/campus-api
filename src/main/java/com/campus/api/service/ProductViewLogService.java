package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.entity.ProductViewLog;

public interface ProductViewLogService {
    /**
     * 添加浏览记录
     */
    void addViewLog(Long userId, Long productId);

    /**
     * 获取用户浏览记录
     */
    PageResult<ProductViewLog> getUserViewLogs(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取商品浏览记录
     */
    PageResult<ProductViewLog> getProductViewLogs(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 清理指定时间之前的浏览记录
     */
    void cleanViewLogs(Long userId, String beforeTime);
} 