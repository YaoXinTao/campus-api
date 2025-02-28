package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.dto.coupon.CouponDTO;
import com.campus.api.entity.Coupon;

import java.util.List;

public interface CouponService {
    /**
     * 创建优惠券
     */
    void createCoupon(CouponDTO couponDTO, Long adminId);

    /**
     * 更新优惠券
     */
    void updateCoupon(CouponDTO couponDTO);

    /**
     * 更新优惠券状态
     */
    void updateCouponStatus(Long id, Integer status);

    /**
     * 获取优惠券详情
     */
    CouponDTO getCouponDetail(Long id);

    /**
     * 获取优惠券列表（管理后台）
     */
    PageResult<CouponDTO> getCouponList(String keyword, Integer type, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取可领取的优惠券列表（小程序）
     */
    List<CouponDTO> getAvailableCoupons();

    /**
     * 用户领取优惠券
     */
    void receiveCoupon(Long userId, Long couponId);

    /**
     * 获取用户优惠券列表
     */
    List<CouponDTO> getUserCoupons(Long userId, Integer status);

    /**
     * 获取用户可用优惠券列表
     */
    List<CouponDTO> getUserAvailableCoupons(Long userId);

    /**
     * 使用优惠券
     */
    void useCoupon(Long userId, Long userCouponId);
} 