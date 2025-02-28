package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.ProductSearchDTO;
import com.campus.api.entity.Product;
import com.campus.api.service.ProductSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品搜索管理")
@RestController
@RequestMapping("/admin/product/search")
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    @Operation(summary = "搜索商品")
    @PostMapping("/list")
    public Result<List<Product>> search(@RequestBody ProductSearchDTO searchDTO) {
        List<Product> list = productSearchService.searchProducts(searchDTO);
        return Result.success(list);
    }

    @Operation(summary = "重建搜索索引")
    @PostMapping("/rebuild-index")
    public Result<Void> rebuildIndex() {
        productSearchService.rebuildAllProductsIndex();
        return Result.success();
    }

    @Operation(summary = "获取相似商品")
    @GetMapping("/similar/{productId}")
    public Result<List<Product>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<Product> similarProducts = productSearchService.getSimilarProducts(productId, page, size);
        return Result.success(similarProducts);
    }
} 