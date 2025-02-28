package com.campus.api.service.impl;

import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.product.ProductSkuDTO;
import com.campus.api.entity.ProductSku;
import com.campus.api.entity.Product;
import com.campus.api.mapper.ProductSkuMapper;
import com.campus.api.mapper.ProductMapper;
import com.campus.api.service.ProductSkuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductSkuServiceImpl implements ProductSkuService {

    @Autowired
    private ProductSkuMapper skuMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ProductSkuDTO getSkuById(Long id) {
        ProductSku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException("SKU不存在");
        }
        return convertToDTO(sku);
    }

    @Override
    public List<ProductSkuDTO> getSkuListByProductId(Long productId) {
        List<ProductSku> skuList = skuMapper.selectByProductId(productId);
        List<ProductSkuDTO> dtoList = new ArrayList<>();
        for (ProductSku sku : skuList) {
            dtoList.add(convertToDTO(sku));
        }
        return dtoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSku(ProductSkuDTO skuDTO) {
        log.info("开始创建SKU, 商品ID: {}, 规格数据: {}", skuDTO.getProductId(), skuDTO.getSpecData());
        ProductSku sku = new ProductSku();
        BeanUtils.copyProperties(skuDTO, sku);
        
        // 设置默认值
        if (sku.getStatus() == null) {
            sku.setStatus(1);
        }
        if (sku.getStock() == null) {
            sku.setStock(0);
        }
        if (sku.getSales() == null) {
            sku.setSales(0);
        }
        if (sku.getPrice() == null) {
            sku.setPrice(BigDecimal.ZERO);
        }
        if (sku.getMarketPrice() == null) {
            sku.setMarketPrice(BigDecimal.ZERO);
        }
        if (sku.getCostPrice() == null) {
            sku.setCostPrice(BigDecimal.ZERO);
        }
        
        try {
            // 处理规格数据
            List<ProductSkuDTO.SpecData> specData = skuDTO.getSpecData();
            log.info("接收到的规格数据: {}", specData);
            
            if (specData != null && !specData.isEmpty()) {
                // 确保规格数据格式正确
                List<Map<String, String>> formattedSpecData = specData.stream()
                    .map(spec -> {
                        Map<String, String> specMap = new HashMap<>();
                        specMap.put("key", spec.getKey());
                        specMap.put("value", spec.getValue());
                        return specMap;
                    })
                    .collect(Collectors.toList());
                    
                // 将规格数据序列化为JSON字符串
                String specDataJson = objectMapper.writeValueAsString(formattedSpecData);
                log.info("处理后的规格数据JSON: {}", specDataJson);
                sku.setSpecData(specDataJson);
                
                // 使用商品ID和规格值组合作为SKU编码
                String specValues = specData.stream()
                    .map(ProductSkuDTO.SpecData::getValue)
                    .collect(Collectors.joining("-"));
                String skuCode = skuDTO.getProductId() + "-" + specValues;
                sku.setSkuCode(skuCode);
                log.info("生成的SKU编码: {}", skuCode);
            } else {
                sku.setSpecData("[]");
                sku.setSkuCode(skuDTO.getProductId() + "-default");
            }

            // 处理SKU图片URL
            String imageUrl = skuDTO.getImageUrl();
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                String trimmedImageUrl = imageUrl.trim();
                sku.setImageUrl(trimmedImageUrl);
                log.info("设置SKU图片URL: {}", trimmedImageUrl);
            } else {
                log.info("SKU图片URL为空");
                sku.setImageUrl(null);
            }
            
            // 保存SKU
            log.info("准备保存SKU数据: {}", sku);
            skuMapper.insert(sku);
            log.info("创建新的SKU成功, ID: {}, 商品ID: {}, SKU编码: {}", sku.getId(), sku.getProductId(), sku.getSkuCode());
            
            // 更新商品总库存和销量
            updateProductStockAndSales(skuDTO.getProductId());
            
        } catch (JsonProcessingException e) {
            log.error("规格数据序列化失败", e);
            throw new BusinessException("规格数据格式错误");
        } catch (Exception e) {
            log.error("创建SKU失败", e);
            throw new BusinessException("创建SKU失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateSku(List<ProductSkuDTO> skuDTOList) {
        log.info("开始批量创建SKU, 数量: {}", skuDTOList.size());
        for (ProductSkuDTO skuDTO : skuDTOList) {
            createSku(skuDTO);
        }
        log.info("批量创建SKU完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSku(ProductSkuDTO skuDTO) {
        log.info("开始更新SKU, ID: {}, 商品ID: {}, 规格数据: {}", skuDTO.getId(), skuDTO.getProductId(), skuDTO.getSpecData());
        if (skuDTO.getId() == null) {
            throw new BusinessException("SKU ID不能为空");
        }
        
        ProductSku sku = new ProductSku();
        BeanUtils.copyProperties(skuDTO, sku);
        
        try {
            // 将规格数据转换为JSON字符串
            String specDataJson = objectMapper.writeValueAsString(skuDTO.getSpecData());
            log.info("规格数据JSON转换结果: {}", specDataJson);
            sku.setSpecData(specDataJson);
            
            // 确保图片URL被正确设置
            String imageUrl = skuDTO.getImageUrl();
            log.info("接收到的SKU图片URL: {}", imageUrl);
            sku.setImageUrl(imageUrl); // 不需要判断是否为空，直接设置
            
            // 更新SKU数据
            int result = skuMapper.update(sku);
            log.info("SKU更新结果: {}, 更新后的图片URL: {}", result > 0 ? "成功" : "失败", sku.getImageUrl());
            
            if (result > 0) {
                // 更新商品总库存和销量
                updateProductStockAndSales(sku.getProductId());
                // 更新商品价格
                updateProductPrice(sku.getProductId());
            }
        } catch (JsonProcessingException e) {
            log.error("规格数据转JSON失败: {}", e.getMessage(), e);
            throw new BusinessException("规格数据格式错误");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSku(Long id) {
        // 检查SKU是否存在
        ProductSku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException("SKU不存在");
        }
        
        // 删除SKU
        skuMapper.deleteById(id);
        
        // 更新商品总库存和销量
        updateProductStockAndSales(sku.getProductId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductId(Long productId) {
        skuMapper.deleteByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(Long id, Integer stock) {
        skuMapper.updateStock(id, stock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSales(Long id, Integer sales) {
        skuMapper.updateSales(id, sales);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndDeductStock(Long id, Integer quantity) {
        ProductSku sku = skuMapper.selectById(id);
        if (sku == null) {
            throw new BusinessException("SKU不存在");
        }
        
        // 检查库存是否充足
        if (sku.getStock() < quantity) {
            return false;
        }
        
        // 扣减库存
        skuMapper.updateStock(id, sku.getStock() - quantity);
        return true;
    }

    /**
     * 更新商品总库存和销量
     */
    private void updateProductStockAndSales(Long productId) {
        // 获取商品所有SKU的库存总和
        Integer totalStock = skuMapper.sumStockByProductId(productId);
        // 获取商品所有SKU的销量总和
        Integer totalSales = skuMapper.sumSalesByProductId(productId);
        
        // 更新商品的总库存和销量
        productMapper.updateStockAndSales(productId, totalStock, totalSales);
    }

    /**
     * 更新商品价格
     */
    private void updateProductPrice(Long productId) {
        List<ProductSku> skus = skuMapper.selectByProductId(productId);
        if (skus != null && !skus.isEmpty()) {
            BigDecimal minPrice = skus.stream()
                .map(ProductSku::getPrice)
                .filter(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            // 更新商品表中的价格
            Product product = new Product();
            product.setId(productId);
            product.setPrice(minPrice);
            productMapper.update(product);
            log.info("更新商品价格成功, 商品ID: {}, 最新价格: {}", productId, minPrice);
        }
    }

    /**
     * 将实体转换为DTO
     */
    private ProductSkuDTO convertToDTO(ProductSku sku) {
        ProductSkuDTO dto = new ProductSkuDTO();
        BeanUtils.copyProperties(sku, dto);
        
        try {
            if (sku.getSpecData() != null) {
                log.info("开始解析SKU规格数据, SKU ID: {}, 原始数据: {}", sku.getId(), sku.getSpecData());
                // 尝试直接解析为List<SpecData>
                try {
                    List<ProductSkuDTO.SpecData> specDataList = objectMapper.readValue(
                        sku.getSpecData(),
                        new TypeReference<List<ProductSkuDTO.SpecData>>() {}
                    );
                    dto.setSpecData(specDataList);
                    log.info("SKU规格数据解析成功, 结果: {}", specDataList);
                } catch (JsonProcessingException e) {
                    log.info("直接解析失败，尝试解析字符串格式: {}", e.getMessage());
                    // 如果直接解析失败，可能是字符串形式的JSON，先解析为字符串
                    String specDataStr = objectMapper.readValue(sku.getSpecData(), String.class);
                    List<ProductSkuDTO.SpecData> specDataList = objectMapper.readValue(
                        specDataStr,
                        new TypeReference<List<ProductSkuDTO.SpecData>>() {}
                    );
                    dto.setSpecData(specDataList);
                    log.info("SKU规格数据通过字符串解析成功, 结果: {}", specDataList);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("JSON转规格数据失败, SKU ID: {}, 错误: {}", sku.getId(), e.getMessage(), e);
            throw new BusinessException("规格数据格式错误");
        }
        
        return dto;
    }
} 