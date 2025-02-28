package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.coupon.CouponDTO;
import com.campus.api.service.CouponService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台优惠券接口", description = "管理后台优惠券相关接口")
@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @Operation(summary = "创建优惠券")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> createCoupon(@Validated @RequestBody CouponDTO couponDTO) {
        Long adminId = SecurityUtils.getCurrentUserId();
        couponService.createCoupon(couponDTO, adminId);
        return Result.success();
    }

    @Operation(summary = "更新优惠券")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> updateCoupon(@Validated @RequestBody CouponDTO couponDTO) {
        couponService.updateCoupon(couponDTO);
        return Result.success();
    }

    @Operation(summary = "更新优惠券状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> updateStatus(
            @Parameter(description = "优惠券ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-已停用 1-正常", required = true) @RequestParam Integer status) {
        couponService.updateCouponStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取优惠券详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<CouponDTO> getCouponDetail(@PathVariable Long id) {
        return Result.success(couponService.getCouponDetail(id));
    }

    @Operation(summary = "获取优惠券列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<PageResult<CouponDTO>> getCouponList(
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "优惠券类型：1-满减券 2-折扣券 3-无门槛券") @RequestParam(required = false) Integer type,
            @Parameter(description = "状态：0-已停用 1-正常") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(couponService.getCouponList(keyword, type, status, pageNum, pageSize));
    }
} 