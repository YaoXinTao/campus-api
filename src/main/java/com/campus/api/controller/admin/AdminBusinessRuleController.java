package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.service.BusinessRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台业务规则接口", description = "管理后台业务规则相关接口")
@RestController
@RequestMapping("/api/v1/admin/business-rules")
@RequiredArgsConstructor
public class AdminBusinessRuleController {

    private final BusinessRuleService businessRuleService;

    @Operation(summary = "获取规则值")
    @GetMapping("/{ruleType}/{ruleKey}")
    public Result<String> getRuleValue(
            @Parameter(description = "规则类型", required = true) @PathVariable String ruleType,
            @Parameter(description = "规则键", required = true) @PathVariable String ruleKey) {
        return Result.success(businessRuleService.getRuleValue(ruleType, ruleKey));
    }

    @Operation(summary = "设置规则值")
    @PostMapping("/{ruleType}/{ruleKey}")
    public Result<Void> setRuleValue(
            @Parameter(description = "规则类型", required = true) @PathVariable String ruleType,
            @Parameter(description = "规则键", required = true) @PathVariable String ruleKey,
            @Parameter(description = "规则值", required = true) @RequestParam String ruleValue) {
        businessRuleService.updateRuleValue(ruleType, ruleKey, ruleValue);
        return Result.success();
    }

    @Operation(summary = "获取布尔类型规则值")
    @GetMapping("/{ruleType}/{ruleKey}/bool")
    public Result<Boolean> getRuleValueAsBoolean(
            @Parameter(description = "规则类型") @PathVariable String ruleType,
            @Parameter(description = "规则键") @PathVariable String ruleKey) {
        return Result.success(businessRuleService.getRuleValueAsBoolean(ruleType, ruleKey));
    }

    @Operation(summary = "获取整数类型规则值")
    @GetMapping("/{ruleType}/{ruleKey}/int")
    public Result<Integer> getRuleValueAsInt(
            @Parameter(description = "规则类型") @PathVariable String ruleType,
            @Parameter(description = "规则键") @PathVariable String ruleKey) {
        return Result.success(businessRuleService.getRuleValueAsInt(ruleType, ruleKey));
    }
} 