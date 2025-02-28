package com.campus.api.service;

import com.campus.api.dto.product.ProductSpecTemplateDTO;
import com.campus.api.dto.product.ProductSpecOptionDTO;
import com.campus.api.dto.product.SkuCombinationDTO;
import java.util.List;

public interface ProductSpecService {
    
    /**
     * 获取规格模板
     */
    ProductSpecTemplateDTO getSpecTemplate(Long id);
    
    /**
     * 获取商品规格选项
     */
    List<ProductSpecOptionDTO> getProductSpecOptions(Long productId);
    
    /**
     * 获取SKU规格组合
     */
    List<SkuCombinationDTO> getSkuCombinations(Long productId);
} 