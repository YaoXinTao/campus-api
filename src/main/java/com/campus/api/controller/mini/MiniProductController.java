package com.campus.api.controller.mini;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.product.ProductDTO;
import com.campus.api.dto.product.ProductQuery;
import com.campus.api.dto.product.ProductReviewDTO;
import com.campus.api.dto.product.ProductFavoriteDTO;
import com.campus.api.dto.ProductTagDTO;
import com.campus.api.entity.ProductFavorite;
import com.campus.api.entity.ProductViewLog;
import com.campus.api.service.ProductFavoriteService;
import com.campus.api.service.ProductReviewService;
import com.campus.api.service.ProductService;
import com.campus.api.service.ProductViewLogService;
import com.campus.api.service.ProductTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.campus.api.util.SecurityUtils;
import com.campus.api.common.exception.BusinessException;

import java.util.List;
import java.util.Map;

@Tag(name = "小程序商品接口", description = "小程序商品相关接口")
@RestController
@RequestMapping("/api/v1/mini/product")
@RequiredArgsConstructor
public class MiniProductController {

    private final ProductFavoriteService favoriteService;
    private final ProductReviewService reviewService;
    private final ProductViewLogService viewLogService;
    private final ProductTagService tagService;

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取商品列表", description = "获取商品列表，支持分页和条件查询")
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public Result<PageResult<ProductDTO>> getProductList(ProductQuery query) {
        // 只查询上架且审核通过的商品
        query.setStatus(1);
        query.setVerifyStatus(1);
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "获取商品详情", description = "根据ID获取商品详情")
    @Parameter(name = "id", description = "商品ID", required = true)
    @GetMapping("/detail/{id}")
    @PreAuthorize("permitAll()")
    public Result<ProductDTO> getProduct(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        // 增加商品浏览量
        productService.incrementViewCount(id);
        return Result.success(product);
    }

    @Operation(summary = "获取推荐商品", description = "获取推荐商品列表")
    @GetMapping("/featured")
    @PreAuthorize("permitAll()")
    public Result<List<ProductDTO>> getFeaturedProducts() {
        ProductQuery query = new ProductQuery();
        query.setStatus(1);
        query.setVerifyStatus(1);
        query.setIsFeatured(1);
        query.setPageSize(10);
        return Result.success(productService.getProductList(query).getList());
    }

    @Operation(summary = "获取分类商品", description = "获取指定分类下的商品列表")
    @Parameter(name = "categoryId", description = "分类ID", required = true)
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("permitAll()")
    public Result<PageResult<ProductDTO>> getCategoryProducts(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setCategoryId(categoryId);
        query.setStatus(1);
        query.setVerifyStatus(1);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "搜索商品", description = "根据关键字搜索商品")
    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    public Result<PageResult<ProductDTO>> searchProducts(
            @Parameter(description = "搜索关键字") @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setKeyword(keyword);
        query.setStatus(1);
        query.setVerifyStatus(1);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "添加收藏")
    @PostMapping("/favorite")
    public Result<Void> addFavorite(@RequestBody Map<String, Long> params) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long productId = params.get("productId");
        if (productId == null) {
            throw new BusinessException("商品ID不能为空");
        }
        favoriteService.addFavorite(userId, productId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{productId}")
    public Result<Void> removeFavorite(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteService.removeFavorite(userId, productId);
        return Result.success();
    }

    @Operation(summary = "获取收藏列表")
    @GetMapping("/favorite/list")
    public Result<PageResult<ProductFavoriteDTO>> getFavoriteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(favoriteService.getUserFavorites(userId, pageNum, pageSize));
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/favorite/check")
    public Result<Boolean> checkFavorite(@RequestParam Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(favoriteService.isFavorited(userId, productId));
    }

    @Operation(summary = "添加评价")
    @PostMapping("/review")
    public Result<Void> addReview(@RequestBody ProductReviewDTO reviewDTO) {
        reviewService.addReview(reviewDTO);
        return Result.success();
    }

    @Operation(summary = "获取商品评价列表")
    @GetMapping("/reviews/{productId}")
    @PreAuthorize("permitAll()")
    public Result<PageResult<ProductReviewDTO>> getReviewList(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(reviewService.getProductReviews(productId, pageNum, pageSize));
    }

    @Operation(summary = "获取相似商品")
    @GetMapping("/similar/{productId}")
    @PreAuthorize("permitAll()")
    public Result<List<ProductDTO>> getSimilarProducts(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(productService.getSimilarProducts(productId, limit));
    }

    @Operation(summary = "获取用户评价列表")
    @GetMapping("/review/user/list")
    public Result<PageResult<ProductReviewDTO>> getUserReviewList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(reviewService.getUserReviews(userId, pageNum, pageSize));
    }

    @Operation(summary = "检查是否已评价")
    @GetMapping("/review/check")
    public Result<Boolean> checkReview(
            @RequestParam Long orderId,
            @RequestParam Long skuId) {
        return Result.success(reviewService.hasReviewed(orderId, skuId));
    }

    @Operation(summary = "添加商品浏览记录")
    @PostMapping("/{productId}/view")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> addViewHistory(@PathVariable Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        productService.addViewHistory(userId, productId);
        return Result.success();
    }

    @Operation(summary = "获取用户浏览记录")
    @GetMapping("/view/list")
    public Result<PageResult<ProductViewLog>> getViewLogList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(viewLogService.getUserViewLogs(userId, pageNum, pageSize));
    }

    @Operation(summary = "清理浏览记录")
    @DeleteMapping("/view/clean")
    public Result<Void> cleanViewLogs(
            @RequestParam Long userId,
            @RequestParam String beforeTime) {
        viewLogService.cleanViewLogs(userId, beforeTime);
        return Result.success();
    }

    @Operation(summary = "获取所有启用的商品标签")
    @GetMapping("/tag/list")
    public Result<List<ProductTagDTO>> getTagList() {
        return Result.success(tagService.getAllEnabledTags());
    }

    @Operation(summary = "获取标签下的商品列表")
    @GetMapping("/tag/{tagId}/products")
    public Result<PageResult<ProductDTO>> getTagProducts(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        ProductQuery query = new ProductQuery();
        query.setTagId(tagId);
        query.setStatus(1);
        query.setVerifyStatus(1);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "获取商品的标签列表")
    @GetMapping("/{productId}/tags")
    public Result<List<ProductTagDTO>> getProductTags(@PathVariable Long productId) {
        return Result.success(tagService.getTagsByProductId(productId));
    }
} 