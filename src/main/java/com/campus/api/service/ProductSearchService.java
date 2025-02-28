package com.campus.api.service;

import com.campus.api.dto.ProductSearchDTO;
import com.campus.api.entity.Product;
import java.util.List;

public interface ProductSearchService {
    
    /**
     * 搜索商品
     */
    List<Product> searchProducts(ProductSearchDTO searchDTO);
    
    /**
     * 更新商品搜索索引
     */
    void updateProductIndex(Product product);
    
    /**
     * 删除商品搜索索引
     */
    void deleteProductIndex(Long productId);
    
    /**
     * 重建所有商品的搜索索引
     */
    void rebuildAllProductsIndex();
    
    /**
     * 获取相似商品推荐
     */
    List<Product> getSimilarProducts(Long productId, Integer page, Integer size);
    
    /**
     * 获取热门搜索关键词
     */
    List<String> getHotSearchKeywords(int limit);
} 