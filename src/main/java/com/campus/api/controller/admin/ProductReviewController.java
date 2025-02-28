package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.entity.ProductReview;
import com.campus.api.service.ProductReviewService;
import com.campus.api.dto.product.ProductReviewQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "商品评价管理", description = "商品评价管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/product/comment")
@SecurityRequirement(name = "bearer-key")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    @Operation(summary = "获取评价列表", description = "分页获取商品评价列表")
    @GetMapping("/list")
    public Result<PageResult<ProductReview>> getList(ProductReviewQuery query) {
        PageResult<ProductReview> pageResult = productReviewService.getReviewList(query);
        return Result.success(pageResult);
    }

    @Operation(summary = "获取评价详情", description = "获取指定评价的详细信息")
    @Parameter(name = "id", description = "评价ID", required = true)
    @GetMapping("/{id}")
    public Result<ProductReview> getDetail(@PathVariable Long id) {
        ProductReview review = productReviewService.getReviewDetail(id);
        return Result.success(review);
    }

    @Operation(summary = "回复评价", description = "商家回复指定评价")
    @Parameter(name = "id", description = "评价ID", required = true)
    @PutMapping("/{id}/reply")
    public Result<Void> reply(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String replyContent = params.get("replyContent");
        productReviewService.replyReview(id, replyContent);
        return Result.success();
    }

    @Operation(summary = "修改回复", description = "修改商家已有的回复")
    @Parameter(name = "id", description = "评价ID", required = true)
    @PutMapping("/{id}/reply/update")
    public Result<Void> updateReply(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String replyContent = params.get("replyContent");
        productReviewService.updateReply(id, replyContent);
        return Result.success();
    }

    @Operation(summary = "删除评价", description = "删除指定评价")
    @Parameter(name = "id", description = "评价ID", required = true)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productReviewService.deleteReview(id);
        return Result.success();
    }

    @Operation(summary = "更新评价状态", description = "更新指定评价的显示状态")
    @Parameter(name = "id", description = "评价ID", required = true)
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        productReviewService.updateReviewStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取商品评分统计", description = "获取指定商品的评分统计信息")
    @Parameter(name = "productId", description = "商品ID", required = true)
    @GetMapping("/stats/{productId}")
    public Result<Map<String, Object>> getRatingStats(@PathVariable Long productId) {
        Map<String, Object> stats = productReviewService.getProductRatingStats(productId);
        return Result.success(stats);
    }

    @Operation(summary = "批量更新评价状态", description = "批量更新多个评价的显示状态")
    @PutMapping("/batch/status")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        Integer status = (Integer) params.get("status");
        productReviewService.batchUpdateReviewStatus(ids, status);
        return Result.success();
    }

    @Operation(summary = "批量删除评价", description = "批量删除多个评价")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        productReviewService.batchDeleteReviews(ids);
        return Result.success();
    }
} 