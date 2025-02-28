package com.campus.api.service.impl;

import com.campus.api.dto.ProductTagDTO;
import com.campus.api.entity.ProductTag;
import com.campus.api.service.ProductTagService;
import com.campus.api.common.PageResult;
import com.campus.api.mapper.ProductTagMapper;
import com.campus.api.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductTagServiceImpl implements ProductTagService {

    private final ProductTagMapper productTagMapper;

    @Override
    public PageResult<ProductTag> getList(String keyword, Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        Long total = productTagMapper.count(keyword);
        
        // 查询数据
        List<ProductTag> list = productTagMapper.selectList(keyword, offset, pageSize);
        
        return new PageResult<ProductTag>(pageNum, pageSize, total, list);
    }

    @Override
    public ProductTag getById(Long id) {
        ProductTag tag = productTagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ProductTagDTO productTagDTO) {
        // 检查名称是否重复
        if (productTagMapper.existsByName(productTagDTO.getName())) {
            throw new BusinessException("标签名称已存在");
        }
        
        ProductTag tag = new ProductTag();
        BeanUtils.copyProperties(productTagDTO, tag);
        
        // 设置默认状态为启用
        tag.setStatus(1);
        
        // 如果排序号为空，则设置默认值为0
        if (tag.getSortOrder() == null) {
            tag.setSortOrder(0);
        }
        
        productTagMapper.insert(tag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, ProductTagDTO productTagDTO) {
        // 检查标签是否存在
        ProductTag existingTag = getById(id);
        
        // 检查名称是否重复(排除自身)
        if (productTagMapper.existsByNameAndNotId(productTagDTO.getName(), id)) {
            throw new BusinessException("标签名称已存在");
        }
        
        BeanUtils.copyProperties(productTagDTO, existingTag);
        productTagMapper.update(existingTag);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查标签是否存在
        getById(id);
        
        // 检查标签是否被使用
        if (productTagMapper.countProductRelations(id) > 0) {
            throw new BusinessException("标签已被商品使用，无法删除");
        }
        
        productTagMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 检查标签是否存在
        getById(id);
        
        productTagMapper.updateStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSetProductTags(Long tagId, List<Long> productIds) {
        // 检查标签是否存在
        getById(tagId);
        
        // 删除原有关联
        productTagMapper.deleteProductRelations(tagId);
        
        // 批量添加新关联
        if (productIds != null && !productIds.isEmpty()) {
            productTagMapper.batchInsertProductRelations(tagId, productIds);
        }
    }

    @Override
    public List<ProductTagDTO> getAllEnabledTags() {
        List<ProductTag> tags = productTagMapper.selectAllEnabled();
        return tags.stream()
                .map(tag -> {
                    ProductTagDTO dto = new ProductTagDTO();
                    BeanUtils.copyProperties(tag, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductTagDTO> getTagsByProductId(Long productId) {
        List<ProductTag> tags = productTagMapper.selectByProductId(productId);
        return tags.stream()
                .map(tag -> {
                    ProductTagDTO dto = new ProductTagDTO();
                    BeanUtils.copyProperties(tag, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 