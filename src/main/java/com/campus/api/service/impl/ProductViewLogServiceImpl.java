package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.entity.ProductViewLog;
import com.campus.api.mapper.ProductViewLogMapper;
import com.campus.api.service.ProductViewLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductViewLogServiceImpl implements ProductViewLogService {

    private final ProductViewLogMapper viewLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addViewLog(Long userId, Long productId) {
        ProductViewLog log = new ProductViewLog();
        log.setUserId(userId);
        log.setProductId(productId);
        viewLogMapper.insert(log);
    }

    @Override
    public PageResult<ProductViewLog> getUserViewLogs(Long userId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = viewLogMapper.countByUserId(userId);
        
        // 查询数据
        List<ProductViewLog> list = viewLogMapper.selectByUserId(userId, offset, pageSize);
        
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    @Override
    public PageResult<ProductViewLog> getProductViewLogs(Long productId, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = viewLogMapper.countByProductId(productId);
        
        // 查询数据
        List<ProductViewLog> list = viewLogMapper.selectByProductId(productId, offset, pageSize);
        
        return new PageResult<>(pageNum, pageSize, total, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanViewLogs(Long userId, String beforeTime) {
        viewLogMapper.deleteByUserIdAndTime(userId, beforeTime);
    }
} 