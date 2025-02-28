package com.campus.api.service.impl;

import com.campus.api.entity.ProductAttribute;
import com.campus.api.mapper.ProductAttributeMapper;
import com.campus.api.service.ProductAttributeService;
import com.campus.api.dto.ProductAttributeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductAttributeServiceImpl implements ProductAttributeService {

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAttribute(ProductAttributeDTO dto) {
        ProductAttribute attribute = new ProductAttribute();
        BeanUtils.copyProperties(dto, attribute);
        productAttributeMapper.insert(attribute);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttribute(ProductAttributeDTO dto) {
        ProductAttribute attribute = productAttributeMapper.selectById(dto.getId());
        if (attribute == null) {
            throw new RuntimeException("属性不存在");
        }
        BeanUtils.copyProperties(dto, attribute);
        productAttributeMapper.update(attribute);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAttribute(Long id) {
        productAttributeMapper.deleteById(id);
    }

    @Override
    public List<ProductAttributeDTO> getAttributesByProductId(Long productId) {
        List<ProductAttribute> attributes = productAttributeMapper.selectByProductId(productId);
        return attributes.stream().map(attr -> {
            ProductAttributeDTO dto = new ProductAttributeDTO();
            BeanUtils.copyProperties(attr, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddAttributes(Long productId, List<ProductAttributeDTO> attributes) {
        // 先删除原有属性
        productAttributeMapper.deleteByProductId(productId);
        
        // 批量添加新属性
        attributes.forEach(dto -> {
            ProductAttribute attribute = new ProductAttribute();
            BeanUtils.copyProperties(dto, attribute);
            attribute.setProductId(productId);
            productAttributeMapper.insert(attribute);
        });
    }
} 