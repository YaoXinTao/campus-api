package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.service.ProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mini/product/spec")
@Tag(name = "小程序商品规格接口", description = "小程序商品规格相关接口")
public class MiniProductSpecController {

    @Autowired
    private ProductSpecService productSpecService;

    @Operation(summary = "获取商品规格模板")
    @GetMapping("/template/{id}")
    public Result getSpecTemplate(@PathVariable Long id) {
        return Result.success(productSpecService.getSpecTemplate(id));
    }

    @Operation(summary = "获取商品规格选项")
    @GetMapping("/options/{productId}")
    public Result getSpecOptions(@PathVariable Long productId) {
        return Result.success(productSpecService.getProductSpecOptions(productId));
    }

    @Operation(summary = "获取SKU规格组合")
    @GetMapping("/sku/combinations/{productId}")
    public Result getSkuCombinations(@PathVariable Long productId) {
        return Result.success(productSpecService.getSkuCombinations(productId));
    }
} 