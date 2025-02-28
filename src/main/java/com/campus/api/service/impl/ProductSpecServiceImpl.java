package com.campus.api.service.impl;

import com.campus.api.dto.product.ProductSpecTemplateDTO;
import com.campus.api.dto.product.ProductSpecOptionDTO;
import com.campus.api.dto.product.SkuCombinationDTO;
import com.campus.api.entity.ProductSpecTemplate;
import com.campus.api.entity.ProductSku;
import com.campus.api.mapper.ProductSpecTemplateMapper;
import com.campus.api.mapper.ProductSkuMapper;
import com.campus.api.service.ProductSpecService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSpecServiceImpl implements ProductSpecService {

    @Autowired
    private ProductSpecTemplateMapper specTemplateMapper;

    @Autowired
    private ProductSkuMapper skuMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProductSpecTemplateDTO getSpecTemplate(Long id) {
        ProductSpecTemplate template = specTemplateMapper.selectById(id);
        if (template == null) {
            return null;
        }
        
        ProductSpecTemplateDTO dto = new ProductSpecTemplateDTO();
        BeanUtils.copyProperties(template, dto);
        
        // 解析规格项JSON数据
        try {
            List<ProductSpecTemplateDTO.SpecItem> specItems = objectMapper.readValue(
                template.getSpecItems(), 
                new TypeReference<List<ProductSpecTemplateDTO.SpecItem>>() {}
            );
            dto.setSpecItems(specItems);
        } catch (Exception e) {
            dto.setSpecItems(Collections.emptyList());
        }
        
        return dto;
    }

    @Override
    public List<ProductSpecOptionDTO> getProductSpecOptions(Long productId) {
        // 获取商品的所有SKU
        List<ProductSku> skuList = skuMapper.selectByProductId(productId);
        if (skuList.isEmpty()) {
            return Collections.emptyList();
        }

        // 解析SKU的规格数据，构建规格选项
        Map<String, Set<String>> specOptionsMap = new HashMap<>();
        for (ProductSku sku : skuList) {
            List<Map<String, String>> specData = parseSpecData(sku.getSpecData());
            for (Map<String, String> spec : specData) {
                String key = spec.get("key");
                String value = spec.get("value");
                specOptionsMap.computeIfAbsent(key, k -> new HashSet<>()).add(value);
            }
        }

        // 转换为DTO列表
        return specOptionsMap.entrySet().stream()
                .map(entry -> {
                    ProductSpecOptionDTO dto = new ProductSpecOptionDTO();
                    dto.setName(entry.getKey());
                    dto.setValues(new ArrayList<>(entry.getValue()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SkuCombinationDTO> getSkuCombinations(Long productId) {
        List<ProductSku> skuList = skuMapper.selectByProductId(productId);
        return skuList.stream()
                .map(sku -> {
                    SkuCombinationDTO dto = new SkuCombinationDTO();
                    BeanUtils.copyProperties(sku, dto);
                    dto.setSpecList(parseSpecData(sku.getSpecData()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> parseSpecData(String specData) {
        try {
            return objectMapper.readValue(specData, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
} 