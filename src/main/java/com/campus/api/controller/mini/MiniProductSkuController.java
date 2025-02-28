package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.product.ProductSkuDTO;
import com.campus.api.service.ProductSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序商品SKU接口", description = "小程序商品SKU相关接口")
@RestController
@RequestMapping("/api/v1/mini/product/sku")
public class MiniProductSkuController {

    @Autowired
    private ProductSkuService skuService;

    @Operation(summary = "获取SKU详情", description = "根据SKU ID获取SKU详情")
    @Parameter(name = "id", description = "SKU ID", required = true)
    @GetMapping("/{id}")
    public Result<ProductSkuDTO> getSku(@PathVariable Long id) {
        return Result.success(skuService.getSkuById(id));
    }

    @Operation(summary = "获取商品SKU列表", description = "根据商品ID获取SKU列表")
    @Parameter(name = "productId", description = "商品ID", required = true)
    @GetMapping("/list/{productId}")
    public Result<List<ProductSkuDTO>> getSkuList(@PathVariable Long productId) {
        return Result.success(skuService.getSkuListByProductId(productId));
    }

    @Operation(summary = "检查SKU库存", description = "检查指定SKU是否有足够库存")
    @GetMapping("/{id}/stock/check")
    public Result<Boolean> checkStock(
            @Parameter(description = "SKU ID") @PathVariable Long id,
            @Parameter(description = "购买数量") @RequestParam Integer quantity) {
        return Result.success(skuService.checkAndDeductStock(id, quantity));
    }
} 