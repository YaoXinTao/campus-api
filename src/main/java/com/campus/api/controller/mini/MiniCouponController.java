package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.coupon.CouponDTO;
import com.campus.api.entity.Coupon;
import com.campus.api.mapper.CouponMapper;
import com.campus.api.service.CouponService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序优惠券接口", description = "小程序优惠券相关接口")
@RestController
@RequestMapping("/api/v1/mini/coupons")
@RequiredArgsConstructor
@Slf4j
public class MiniCouponController {

    private final CouponService couponService;
    private final CouponMapper couponMapper;

    @Operation(summary = "获取可领取的优惠券列表")
    @GetMapping("/available")
    public Result<List<CouponDTO>> getAvailableCoupons(
            @Parameter(description = "是否包含调试信息") 
            @RequestParam(required = false, defaultValue = "false") Boolean debug) {
        
        if (debug) {
            log.info("开始查询优惠券（调试模式）");
            
            // 查询不同条件下的优惠券列表
            List<Coupon> activeOnly = couponMapper.selectActiveOnly();
            List<Coupon> validTimeOnly = couponMapper.selectValidTimeOnly();
            List<Coupon> hasRemainOnly = couponMapper.selectHasRemainOnly();
            
            log.info("状态为启用的优惠券数量: {}", activeOnly.size());
            log.info("在有效期内的优惠券数量: {}", validTimeOnly.size());
            log.info("还有剩余数量的优惠券数量: {}", hasRemainOnly.size());
            
            // 记录每个优惠券的详细信息
            List<Coupon> allCoupons = couponMapper.selectList(null, null, null, 0, 100);
            for (Coupon coupon : allCoupons) {
                log.info("优惠券[{}]状态:", coupon.getName());
                log.info("- ID: {}", coupon.getId());
                log.info("- 状态: {}", coupon.getStatus());
                log.info("- 剩余数量: {}", coupon.getRemainCount());
                log.info("- 开始时间: {}", coupon.getStartTime());
                log.info("- 结束时间: {}", coupon.getEndTime());
            }
        }
        
        return Result.success(couponService.getAvailableCoupons());
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/{id}/receive")
    public Result<Void> receiveCoupon(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        couponService.receiveCoupon(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取我的优惠券列表")
    @GetMapping("/my")
    public Result<List<CouponDTO>> getMyCoupons(
            @Parameter(description = "状态：0-已作废 1-未使用 2-已使用 3-已过期") 
            @RequestParam(required = false) Integer status) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(couponService.getUserCoupons(userId, status));
    }

    @Operation(summary = "获取可用优惠券列表")
    @GetMapping("/my/available")
    public Result<List<CouponDTO>> getMyAvailableCoupons() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(couponService.getUserAvailableCoupons(userId));
    }

    @Operation(summary = "使用优惠券")
    @PostMapping("/{id}/use")
    public Result<Void> useCoupon(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        couponService.useCoupon(userId, id);
        return Result.success();
    }
} 