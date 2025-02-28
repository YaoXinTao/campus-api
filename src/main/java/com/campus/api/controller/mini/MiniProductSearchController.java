package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.ProductSearchDTO;
import com.campus.api.entity.Product;
import com.campus.api.service.ProductSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序-商品搜索接口")
@RestController
@RequestMapping("/mini/product/search")
public class MiniProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;

    @Operation(summary = "搜索商品")
    @PostMapping("/list")
    public Result<List<Product>> search(@RequestBody ProductSearchDTO searchDTO) {
        List<Product> list = productSearchService.searchProducts(searchDTO);
        return Result.success(list);
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

    @Operation(summary = "获取热门搜索关键词")
    @GetMapping("/hot-keywords")
    public Result<List<String>> getHotKeywords(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<String> keywords = productSearchService.getHotSearchKeywords(limit);
        return Result.success(keywords);
    }
} 