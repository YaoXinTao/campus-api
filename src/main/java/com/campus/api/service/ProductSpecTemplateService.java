package com.campus.api.service;

import com.campus.api.dto.product.ProductSpecTemplateDTO;
import java.util.List;

public interface ProductSpecTemplateService {
    /**
     * 获取规格模板详情
     */
    ProductSpecTemplateDTO getTemplateById(Long id);

    /**
     * 获取规格模板列表
     */
    List<ProductSpecTemplateDTO> getTemplateList();

    /**
     * 创建规格模板
     */
    void createTemplate(ProductSpecTemplateDTO templateDTO);

    /**
     * 更新规格模板
     */
    void updateTemplate(ProductSpecTemplateDTO templateDTO);

    /**
     * 删除规格模板
     */
    void deleteTemplate(Long id);

    /**
     * 更新规格模板状态
     */
    void updateTemplateStatus(Long id, Integer status);
} 