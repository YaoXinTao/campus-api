package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.entity.ProductFavorite;
import com.campus.api.dto.product.ProductFavoriteDTO;
import com.campus.api.mapper.ProductFavoriteMapper;
import com.campus.api.service.ProductFavoriteService;
import com.campus.api.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFavoriteServiceImpl implements ProductFavoriteService {

    private final ProductFavoriteMapper favoriteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long productId) {
        // 检查是否已收藏
        if (favoriteMapper.existsByUserIdAndProductId(userId, productId) > 0) {
            throw new BusinessException("已收藏该商品");
        }
        
        // 添加收藏
        ProductFavorite favorite = new ProductFavorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteMapper.insert(favorite);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(Long userId, Long productId) {
        favoriteMapper.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public PageResult<ProductFavoriteDTO> getUserFavorites(Long userId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = favoriteMapper.countByUserId(userId);
        
        // 查询数据
        List<ProductFavoriteDTO> list = favoriteMapper.selectFavoriteProducts(userId, offset, pageSize);
        
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    @Override
    public boolean isFavorited(Long userId, Long productId) {
        return favoriteMapper.existsByUserIdAndProductId(userId, productId) > 0;
    }
} 