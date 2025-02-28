package com.campus.api.mapper;

import com.campus.api.entity.BusinessRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BusinessRuleMapper {
    @Select("SELECT * FROM business_rules WHERE rule_type = #{ruleType} AND rule_key = #{ruleKey} AND status = 1")
    BusinessRule selectByTypeAndKey(String ruleType, String ruleKey);

    @Select("SELECT rule_value FROM business_rules WHERE rule_type = #{ruleType} AND rule_key = #{ruleKey} AND status = 1")
    String selectRuleValue(String ruleType, String ruleKey);

    @Update("UPDATE business_rules SET rule_value = #{ruleValue}, updated_at = NOW() WHERE rule_type = #{ruleType} AND rule_key = #{ruleKey}")
    int updateRuleValue(String ruleType, String ruleKey, String ruleValue);
} 