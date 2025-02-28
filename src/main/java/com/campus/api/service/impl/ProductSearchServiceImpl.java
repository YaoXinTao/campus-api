package com.campus.api.service.impl;

import com.campus.api.dto.ProductSearchDTO;
import com.campus.api.entity.Product;
import com.campus.api.mapper.ProductMapper;
import com.campus.api.mapper.ProductTagRelationMapper;
import com.campus.api.service.ProductSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSearchServiceImpl implements ProductSearchService {

    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductTagRelationMapper tagRelationMapper;

    @Override
    public List<Product> searchProducts(ProductSearchDTO searchDTO) {
        // 构建查询条件
        String keyword = searchDTO.getKeyword();
        Long categoryId = searchDTO.getCategoryId();
        Long tagId = searchDTO.getTagId();
        Integer minPrice = searchDTO.getMinPrice();
        Integer maxPrice = searchDTO.getMaxPrice();
        Boolean onlyInStock = searchDTO.getOnlyInStock();
        Boolean featured = searchDTO.getFeatured();
        String sortField = searchDTO.getSortField();
        String sortOrder = searchDTO.getSortOrder();
        
        // 计算分页参数
        int offset = (searchDTO.getPage() - 1) * searchDTO.getSize();
        int limit = searchDTO.getSize();
        
        // 如果有标签过滤，先获取标签关联的商品ID
        List<Long> productIds = null;
        if (tagId != null) {
            productIds = tagRelationMapper.selectProductIdsByTagId(tagId);
            if (productIds.isEmpty()) {
                return new ArrayList<>();
            }
        }
        
        return productMapper.searchProducts(
            keyword, categoryId, productIds, minPrice, maxPrice,
            onlyInStock, featured, sortField, sortOrder,
            offset, limit
        );
    }

    @Override
    public void updateProductIndex(Product product) {
        // 使用数据库搜索，无需额外处理
    }

    @Override
    public void deleteProductIndex(Long productId) {
        // 使用数据库搜索，无需额外处理
    }

    @Override
    public void rebuildAllProductsIndex() {
        // 使用数据库搜索，无需额外处理
    }

    @Override
    public List<Product> getSimilarProducts(Long productId, Integer page, Integer size) {
        Product currentProduct = productMapper.selectById(productId);
        if (currentProduct == null) {
            return new ArrayList<>();
        }
        
        int offset = (page - 1) * size;
        return productMapper.selectSimilarProducts(
            currentProduct.getCategoryId(),
            productId,
            offset,
            size
        );
    }

    @Override
    public List<String> getHotSearchKeywords(int limit) {
        // 可以从Redis获取热门搜索词
        return new ArrayList<>();
    }
} 