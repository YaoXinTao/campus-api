package com.campus.api.service.impl;

import com.campus.api.entity.BusinessRule;
import com.campus.api.service.BusinessRuleService;
import com.campus.api.mapper.BusinessRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BusinessRuleServiceImpl implements BusinessRuleService {

    @Autowired
    private BusinessRuleMapper businessRuleMapper;

    @Override
    @Cacheable(value = "business_rules", key = "#ruleType + ':' + #ruleKey")
    public String getRuleValue(String ruleType, String ruleKey) {
        return businessRuleMapper.selectRuleValue(ruleType, ruleKey);
    }

    @Override
    @Cacheable(value = "business_rules", key = "#ruleType + ':' + #ruleKey")
    public Integer getRuleValueAsInt(String ruleType, String ruleKey) {
        String value = getRuleValue(ruleType, ruleKey);
        return value != null ? Integer.parseInt(value) : null;
    }

    @Override
    @Cacheable(value = "business_rules", key = "#ruleType + ':' + #ruleKey")
    public Boolean getRuleValueAsBoolean(String ruleType, String ruleKey) {
        String value = getRuleValue(ruleType, ruleKey);
        return value != null ? "1".equals(value) || "true".equalsIgnoreCase(value) : null;
    }

    @Override
    @CacheEvict(value = "business_rules", key = "#ruleType + ':' + #ruleKey")
    public void updateRuleValue(String ruleType, String ruleKey, String ruleValue) {
        businessRuleMapper.updateRuleValue(ruleType, ruleKey, ruleValue);
    }

    @Override
    public BusinessRule getRule(String ruleType, String ruleKey) {
        return businessRuleMapper.selectByTypeAndKey(ruleType, ruleKey);
    }
} 