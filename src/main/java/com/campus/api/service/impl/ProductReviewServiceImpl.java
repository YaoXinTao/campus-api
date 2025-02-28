package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.entity.ProductReview;
import com.campus.api.mapper.ProductReviewMapper;
import com.campus.api.service.ProductReviewService;
import com.campus.api.dto.product.ProductReviewQuery;
import com.campus.api.dto.product.ProductReviewDTO;
import com.campus.api.common.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewMapper reviewMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReview(ProductReviewDTO reviewDTO) {
        // 检查是否已评价
        if (reviewMapper.existsByOrderIdAndSkuId(reviewDTO.getOrderId(), reviewDTO.getSkuId()) > 0) {
            throw new BusinessException("该商品已评价");
        }
        
        ProductReview review = new ProductReview();
        BeanUtils.copyProperties(reviewDTO, review);
        
        // 转换图片列表为JSON字符串
        try {
            review.setImages(objectMapper.writeValueAsString(reviewDTO.getImages()));
        } catch (JsonProcessingException e) {
            log.error("评价图片转JSON失败", e);
            throw new BusinessException("评价图片格式错误");
        }
        
        // 设置默认状态为显示
        review.setStatus(1);
        
        reviewMapper.insert(review);
    }

    @Override
    public PageResult<ProductReviewDTO> getProductReviews(Long productId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = reviewMapper.countByProductId(productId);
        
        // 查询数据
        List<ProductReview> list = reviewMapper.selectByProductId(productId, offset, pageSize);
        
        // 转换为DTO
        List<ProductReviewDTO> dtoList = new ArrayList<>();
        for (ProductReview review : list) {
            ProductReviewDTO dto = convertToDTO(review);
            dtoList.add(dto);
        }
        
        return new PageResult<ProductReviewDTO>(pageNum, pageSize, total, dtoList);
    }

    @Override
    public PageResult<ProductReviewDTO> getUserReviews(Long userId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = reviewMapper.countByUserId(userId);
        
        // 查询数据
        List<ProductReview> list = reviewMapper.selectByUserId(userId, offset, pageSize);
        
        // 转换为DTO
        List<ProductReviewDTO> dtoList = new ArrayList<>();
        for (ProductReview review : list) {
            ProductReviewDTO dto = convertToDTO(review);
            dtoList.add(dto);
        }
        
        return new PageResult<ProductReviewDTO>(pageNum, pageSize, total, dtoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyReview(Long id, String replyContent) {
        ProductReview review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 检查是否已回复
        if (review.getReply() != null && !review.getReply().isEmpty()) {
            throw new BusinessException("该评价已回复，不能重复回复");
        }
        
        review.setReply(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReply(Long id, String replyContent) {
        ProductReview review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 检查是否已回复
        if (review.getReply() == null || review.getReply().isEmpty()) {
            throw new BusinessException("该评价尚未回复，请使用回复功能");
        }
        
        review.setReply(replyContent);
        review.setReplyTime(LocalDateTime.now());
        reviewMapper.updateById(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReviewStatus(Long id, Integer status) {
        reviewMapper.updateStatus(id, status);
    }

    @Override
    public boolean hasReviewed(Long orderId, Long skuId) {
        return reviewMapper.existsByOrderIdAndSkuId(orderId, skuId) > 0;
    }

    @Override
    public PageResult<ProductReview> getReviewList(ProductReviewQuery query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<ProductReview> page = (Page<ProductReview>) reviewMapper.selectList(query);
        return new PageResult<ProductReview>(query.getPageNum(), query.getPageSize(), page.getTotal(), page.getResult());
    }

    @Override
    public ProductReview getReviewDetail(Long id) {
        ProductReview review = reviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        return review;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long id) {
        int rows = reviewMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException("评价不存在");
        }
        log.info("商品评价删除成功，评价ID：{}", id);
    }

    @Override
    public Map<String, Object> getProductRatingStats(Long productId) {
        Map<String, Object> stats = new HashMap<>();
        // 获取评分统计
        List<Map<String, Object>> ratingStats = reviewMapper.selectRatingStats(productId);
        // 计算平均分
        if (!ratingStats.isEmpty()) {
            double avgRating = ratingStats.stream()
                    .mapToDouble(map -> {
                        int rating = ((Number) map.get("rating")).intValue();
                        long count = ((Number) map.get("count")).longValue();
                        return rating * count;
                    })
                    .sum() / ratingStats.stream()
                    .mapToLong(map -> ((Number) map.get("count")).longValue())
                    .sum();
            
            stats.put("avgRating", Math.round(avgRating * 10) / 10.0);
        } else {
            stats.put("avgRating", 0.0);
        }
        stats.put("ratingStats", ratingStats);
        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateReviewStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要操作的评价");
        }
        reviewMapper.batchUpdateStatus(ids, status);
        log.info("批量更新评价状态成功，评价IDs：{}，状态：{}", ids, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteReviews(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的评价");
        }
        reviewMapper.batchDelete(ids);
        log.info("批量删除评价成功，评价IDs：{}", ids);
    }

    /**
     * 将实体转换为DTO
     */
    private ProductReviewDTO convertToDTO(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        BeanUtils.copyProperties(review, dto);
        
        // 转换图片JSON为列表
        try {
            if (review.getImages() != null) {
                @SuppressWarnings("unchecked")
                List<String> images = objectMapper.readValue(review.getImages(), List.class);
                dto.setImages(images);
            } else {
                dto.setImages(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            log.error("评价图片JSON转换失败", e);
            dto.setImages(new ArrayList<>());
        }
        
        return dto;
    }
} 