package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.product.ProductSkuDTO;
import com.campus.api.service.ProductSkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品SKU管理", description = "商品SKU相关接口")
@RestController
@RequestMapping("/api/v1/admin/product/sku")
public class ProductSkuController {

    @Autowired
    private ProductSkuService skuService;

    @Operation(summary = "获取SKU详情")
    @GetMapping("/{id}")
    public Result<ProductSkuDTO> getSku(@PathVariable Long id) {
        return Result.success(skuService.getSkuById(id));
    }

    @Operation(summary = "根据商品ID获取SKU列表")
    @GetMapping("/list/{productId}")
    public Result<List<ProductSkuDTO>> getSkuList(@PathVariable Long productId) {
        return Result.success(skuService.getSkuListByProductId(productId));
    }

    @Operation(summary = "创建SKU")
    @PostMapping
    public Result<Void> createSku(@RequestBody ProductSkuDTO skuDTO) {
        skuService.createSku(skuDTO);
        return Result.success();
    }

    @Operation(summary = "批量创建SKU")
    @PostMapping("/batch")
    public Result<Void> batchCreateSku(@RequestBody List<ProductSkuDTO> skuDTOList) {
        skuService.batchCreateSku(skuDTOList);
        return Result.success();
    }

    @Operation(summary = "更新SKU")
    @PutMapping
    public Result<Void> updateSku(@RequestBody ProductSkuDTO skuDTO) {
        skuService.updateSku(skuDTO);
        return Result.success();
    }

    @Operation(summary = "删除SKU")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSku(@PathVariable Long id) {
        skuService.deleteSku(id);
        return Result.success();
    }

    @Operation(summary = "根据商品ID删除SKU")
    @DeleteMapping("/product/{productId}")
    public Result<Void> deleteByProductId(@PathVariable Long productId) {
        skuService.deleteByProductId(productId);
        return Result.success();
    }

    @Operation(summary = "更新SKU库存")
    @PutMapping("/{id}/stock")
    public Result<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        skuService.updateStock(id, stock);
        return Result.success();
    }

    @Operation(summary = "更新SKU销量")
    @PutMapping("/{id}/sales")
    public Result<Void> updateSales(
            @PathVariable Long id,
            @RequestParam Integer sales) {
        skuService.updateSales(id, sales);
        return Result.success();
    }
} 