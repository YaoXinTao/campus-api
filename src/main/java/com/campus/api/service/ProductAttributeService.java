package com.campus.api.service;

import com.campus.api.entity.ProductAttribute;
import com.campus.api.dto.ProductAttributeDTO;
import java.util.List;

public interface ProductAttributeService {
    
    /**
     * 添加商品属性
     */
    void addAttribute(ProductAttributeDTO dto);
    
    /**
     * 修改商品属性
     */
    void updateAttribute(ProductAttributeDTO dto);
    
    /**
     * 删除商品属性
     */
    void deleteAttribute(Long id);
    
    /**
     * 获取商品的所有属性
     */
    List<ProductAttributeDTO> getAttributesByProductId(Long productId);
    
    /**
     * 批量添加商品属性
     */
    void batchAddAttributes(Long productId, List<ProductAttributeDTO> attributes);
} 