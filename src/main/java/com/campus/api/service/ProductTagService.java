package com.campus.api.service;

import com.campus.api.dto.ProductTagDTO;
import com.campus.api.entity.ProductTag;
import com.campus.api.common.PageResult;
import java.util.List;

public interface ProductTagService {
    PageResult<ProductTag> getList(String keyword, Integer pageNum, Integer pageSize);
    
    ProductTag getById(Long id);
    
    void add(ProductTagDTO productTagDTO);
    
    void update(Long id, ProductTagDTO productTagDTO);
    
    void delete(Long id);
    
    void updateStatus(Long id, Integer status);

    void batchSetProductTags(Long tagId, List<Long> productIds);
    
    List<ProductTagDTO> getAllEnabledTags();
    
    List<ProductTagDTO> getTagsByProductId(Long productId);
} 