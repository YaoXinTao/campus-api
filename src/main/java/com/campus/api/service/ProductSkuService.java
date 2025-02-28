package com.campus.api.service;

import com.campus.api.dto.product.ProductSkuDTO;
import java.util.List;

public interface ProductSkuService {
    /**
     * 获取SKU详情
     */
    ProductSkuDTO getSkuById(Long id);

    /**
     * 根据商品ID获取SKU列表
     */
    List<ProductSkuDTO> getSkuListByProductId(Long productId);

    /**
     * 创建SKU
     */
    void createSku(ProductSkuDTO skuDTO);

    /**
     * 批量创建SKU
     */
    void batchCreateSku(List<ProductSkuDTO> skuDTOList);

    /**
     * 更新SKU
     */
    void updateSku(ProductSkuDTO skuDTO);

    /**
     * 根据商品ID删除SKU
     */
    void deleteByProductId(Long productId);

    /**
     * 更新SKU库存
     */
    void updateStock(Long id, Integer stock);

    /**
     * 更新SKU销量
     */
    void updateSales(Long id, Integer sales);

    /**
     * 检查并扣减库存
     */
    boolean checkAndDeductStock(Long id, Integer quantity);

    /**
     * 删除SKU
     */
    void deleteSku(Long id);
} 