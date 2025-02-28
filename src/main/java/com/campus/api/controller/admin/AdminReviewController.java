package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.ReviewListDTO;
import com.campus.api.service.OrderReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员评价接口", description = "后台评价管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/review")
@RequiredArgsConstructor
public class AdminReviewController {

    private final OrderReviewService orderReviewService;

    @Operation(summary = "回复评价")
    @PostMapping("/{id}/reply")
    public Result<Void> replyReview(
            @Parameter(description = "评价ID") @PathVariable Long id,
            @Parameter(description = "回复内容") @RequestParam String content) {
        orderReviewService.replyReview(id, content);
        return Result.success();
    }

    @Operation(summary = "获取商品评价列表")
    @GetMapping("/product/{productId}")
    public Result<PageResult<ReviewListDTO>> getProductReviews(
            @Parameter(description = "商品ID") @PathVariable Long productId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderReviewService.getProductReviews(productId, pageNum, pageSize));
    }
} 