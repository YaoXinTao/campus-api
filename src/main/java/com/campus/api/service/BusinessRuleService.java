package com.campus.api.service;

import com.campus.api.entity.BusinessRule;

public interface BusinessRuleService {
    /**
     * 获取规则值
     *
     * @param ruleType 规则类型
     * @param ruleKey 规则键
     * @return 规则值
     */
    String getRuleValue(String ruleType, String ruleKey);

    /**
     * 获取规则值并转换为Integer
     *
     * @param ruleType 规则类型
     * @param ruleKey 规则键
     * @return 规则值
     */
    Integer getRuleValueAsInt(String ruleType, String ruleKey);

    /**
     * 获取规则值并转换为Boolean
     *
     * @param ruleType 规则类型
     * @param ruleKey 规则键
     * @return 规则值
     */
    Boolean getRuleValueAsBoolean(String ruleType, String ruleKey);

    /**
     * 更新规则值
     *
     * @param ruleType 规则类型
     * @param ruleKey 规则键
     * @param ruleValue 规则值
     */
    void updateRuleValue(String ruleType, String ruleKey, String ruleValue);

    BusinessRule getRule(String ruleType, String ruleKey);
} 