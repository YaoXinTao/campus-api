package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.coupon.CouponDTO;
import com.campus.api.entity.Coupon;
import com.campus.api.entity.UserCoupon;
import com.campus.api.mapper.CouponMapper;
import com.campus.api.mapper.UserCouponMapper;
import com.campus.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCoupon(CouponDTO couponDTO, Long adminId) {
        // 参数校验
        validateCouponParams(couponDTO);
        
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        coupon.setRemainCount(couponDTO.getTotalCount());
        coupon.setCreatedBy(adminId);
        coupon.setStatus(1);
        
        couponMapper.insert(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoupon(CouponDTO couponDTO) {
        // 参数校验
        validateCouponParams(couponDTO);
        
        // 检查优惠券是否存在
        Coupon existingCoupon = couponMapper.selectById(couponDTO.getId());
        if (existingCoupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        Coupon coupon = new Coupon();
        BeanUtils.copyProperties(couponDTO, coupon);
        
        // 如果修改了发行总量，同步修改剩余数量
        if (!existingCoupon.getTotalCount().equals(couponDTO.getTotalCount())) {
            // 计算已领取数量
            int usedCount = existingCoupon.getTotalCount() - existingCoupon.getRemainCount();
            // 新的剩余数量 = 新的总量 - 已领取数量
            int newRemainCount = couponDTO.getTotalCount() - usedCount;
            // 确保剩余数量不小于0
            coupon.setRemainCount(Math.max(0, newRemainCount));
        } else {
            // 如果总量没有改变，保持原有的剩余数量
            coupon.setRemainCount(existingCoupon.getRemainCount());
        }
        
        if (couponMapper.update(coupon) == 0) {
            throw new BusinessException("更新优惠券失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCouponStatus(Long id, Integer status) {
        // 参数校验
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值无效");
        }
        
        if (couponMapper.updateStatus(id, status) == 0) {
            throw new BusinessException("更新优惠券状态失败");
        }
    }

    @Override
    public CouponDTO getCouponDetail(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        return convertToDTO(coupon);
    }

    @Override
    public PageResult<CouponDTO> getCouponList(String keyword, Integer type, Integer status, 
                                             Integer pageNum, Integer pageSize) {
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 查询总数
        long total = couponMapper.selectCount(keyword, type, status);
        if (total == 0) {
            return PageResult.empty(pageNum, pageSize);
        }
        
        // 查询列表数据
        List<Coupon> list = couponMapper.selectList(keyword, type, status, offset, pageSize);
        List<CouponDTO> dtoList = list.stream().map(this::convertToDTO).collect(Collectors.toList());
        
        return PageResult.of(pageNum, pageSize, total, dtoList);
    }

    @Override
    public List<CouponDTO> getAvailableCoupons() {
        log.info("开始查询可领取的优惠券列表");
        
        // 先查询所有优惠券，用于调试
        List<Coupon> allCoupons = couponMapper.selectList(null, null, null, 0, 100);
        log.info("系统中所有优惠券: {}", allCoupons);
        
        // 查询可领取的优惠券
        List<Coupon> availableCoupons = couponMapper.selectAvailable();
        log.info("可领取的优惠券: {}", availableCoupons);
        
        if (availableCoupons.isEmpty()) {
            // 如果没有可领取的优惠券，检查每个优惠券的状态
            for (Coupon coupon : allCoupons) {
                LocalDateTime now = LocalDateTime.now();
                log.info("优惠券[{}]状态检查:", coupon.getName());
                log.info("- 状态: {}", coupon.getStatus() == 1 ? "启用" : "停用");
                log.info("- 剩余数量: {}", coupon.getRemainCount());
                log.info("- 开始时间: {}", coupon.getStartTime());
                log.info("- 结束时间: {}", coupon.getEndTime());
                log.info("- 是否在有效期: {}", 
                    (now.isAfter(coupon.getStartTime()) && now.isBefore(coupon.getEndTime())));
            }
        }
        
        return availableCoupons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long userId, Long couponId) {
        // 检查优惠券是否存在且可领取
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new BusinessException("优惠券不存在或已下架");
        }
        
        if (coupon.getStatus() != 1) {
            throw new BusinessException("优惠券已下架");
        }
        
        // 只检查是否过期，不检查是否开始
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券已过期，看看其他优惠券吧");
        }
        
        if (coupon.getRemainCount() <= 0) {
            throw new BusinessException("手慢了，优惠券已被抢光");
        }
        
        // 检查用户领取数量
        int count = userCouponMapper.countUserCoupon(userId, couponId);
        if (count >= coupon.getPerLimit()) {
            throw new BusinessException("您已领取过该优惠券");
        }
        
        // 扣减库存
        if (couponMapper.decrementRemainCount(couponId) == 0) {
            throw new BusinessException("手慢了，优惠券已被抢光");
        }
        
        // 保存领取记录
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(1);  // 设置为未使用状态
        userCoupon.setReceiveTime(now);
        
        userCouponMapper.insert(userCoupon);
    }

    @Override
    public List<CouponDTO> getUserCoupons(Long userId, Integer status) {
        List<UserCoupon> userCoupons = userCouponMapper.selectByStatus(userId, status);
        return userCoupons.stream()
                .map(uc -> convertToDTO(couponMapper.selectById(uc.getCouponId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponDTO> getUserAvailableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.selectAvailable(userId);
        return userCoupons.stream()
                .map(uc -> convertToDTO(couponMapper.selectById(uc.getCouponId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectByIdAndUserId(userCouponId, userId);
        if (userCoupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        if (userCoupon.getStatus() != 1) {
            throw new BusinessException("优惠券状态无效");
        }
        
        // 检查优惠券是否在有效期内
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new BusinessException("优惠券已过期");
        }
        
        // 更新优惠券状态
        userCoupon.setStatus(2); // 2-已使用
        userCoupon.setUseTime(LocalDateTime.now());
        
        // 使用乐观锁更新状态
        int updateResult = userCouponMapper.updateStatusWithVersion(
            userCoupon.getId(),
            userCoupon.getStatus(),
            userCoupon.getUseTime(),
            userCoupon.getVersion()
        );
        if (updateResult == 0) {
            throw new BusinessException("优惠券已被使用，请重新操作");
        }
    }

    private void validateCouponParams(CouponDTO couponDTO) {
        if (couponDTO.getStartTime().isAfter(couponDTO.getEndTime())) {
            throw new BusinessException("生效时间不能晚于失效时间");
        }
        
        if (couponDTO.getType() == 1 && couponDTO.getMinSpend() == null) {
            throw new BusinessException("满减券必须设置最低消费金额");
        }
        
        if (couponDTO.getType() == 2 && 
            (couponDTO.getAmount().compareTo(new java.math.BigDecimal("10")) > 0 || 
             couponDTO.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0)) {
            throw new BusinessException("折扣率必须在0-10之间");
        }
    }

    private CouponDTO convertToDTO(Coupon coupon) {
        if (coupon == null) {
            return null;
        }
        CouponDTO dto = new CouponDTO();
        BeanUtils.copyProperties(coupon, dto);
        return dto;
    }
} 