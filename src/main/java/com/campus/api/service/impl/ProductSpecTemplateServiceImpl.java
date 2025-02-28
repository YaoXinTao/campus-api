package com.campus.api.service.impl;

import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.product.ProductSpecTemplateDTO;
import com.campus.api.entity.ProductSpecTemplate;
import com.campus.api.mapper.ProductSpecTemplateMapper;
import com.campus.api.service.ProductSpecTemplateService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductSpecTemplateServiceImpl implements ProductSpecTemplateService {

    @Autowired
    private ProductSpecTemplateMapper templateMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ProductSpecTemplateDTO getTemplateById(Long id) {
        ProductSpecTemplate template = templateMapper.selectById(id);
        if (template == null) {
            throw new BusinessException("规格模板不存在");
        }
        return convertToDTO(template);
    }

    @Override
    public List<ProductSpecTemplateDTO> getTemplateList() {
        List<ProductSpecTemplate> templates = templateMapper.selectList();
        List<ProductSpecTemplateDTO> dtoList = new ArrayList<>();
        for (ProductSpecTemplate template : templates) {
            dtoList.add(convertToDTO(template));
        }
        return dtoList;
    }

    @Override
    public void createTemplate(ProductSpecTemplateDTO templateDTO) {
        ProductSpecTemplate template = new ProductSpecTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        
        try {
            // 将规格项列表转换为JSON字符串
            template.setSpecItems(objectMapper.writeValueAsString(templateDTO.getSpecItems()));
        } catch (JsonProcessingException e) {
            log.error("规格项转JSON失败", e);
            throw new BusinessException("规格项格式错误");
        }
        
        templateMapper.insert(template);
    }

    @Override
    public void updateTemplate(ProductSpecTemplateDTO templateDTO) {
        if (templateDTO.getId() == null) {
            throw new BusinessException("模板ID不能为空");
        }
        
        ProductSpecTemplate template = new ProductSpecTemplate();
        BeanUtils.copyProperties(templateDTO, template);
        
        try {
            // 将规格项列表转换为JSON字符串
            template.setSpecItems(objectMapper.writeValueAsString(templateDTO.getSpecItems()));
        } catch (JsonProcessingException e) {
            log.error("规格项转JSON失败", e);
            throw new BusinessException("规格项格式错误");
        }
        
        templateMapper.update(template);
    }

    @Override
    public void deleteTemplate(Long id) {
        templateMapper.delete(id);
    }

    @Override
    public void updateTemplateStatus(Long id, Integer status) {
        templateMapper.updateStatus(id, status);
    }

    /**
     * 将实体转换为DTO
     */
    private ProductSpecTemplateDTO convertToDTO(ProductSpecTemplate template) {
        ProductSpecTemplateDTO dto = new ProductSpecTemplateDTO();
        BeanUtils.copyProperties(template, dto);
        
        try {
            // 将JSON字符串转换为规格项列表
            List<ProductSpecTemplateDTO.SpecItem> specItems = objectMapper.readValue(
                template.getSpecItems(),
                new TypeReference<List<ProductSpecTemplateDTO.SpecItem>>() {}
            );
            dto.setSpecItems(specItems);
        } catch (JsonProcessingException e) {
            log.error("JSON转规格项失败", e);
            throw new BusinessException("规格项格式错误");
        }
        
        return dto;
    }
}