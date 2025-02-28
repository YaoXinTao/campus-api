package com.campus.api.task;

import com.campus.api.mapper.UserCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponTask {

    private final UserCouponMapper userCouponMapper;

    /**
     * 每天凌晨1点执行过期优惠券处理
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void handleExpiredCoupons() {
        log.info("开始处理过期优惠券");
        try {
            int count = userCouponMapper.updateExpiredCoupons();
            log.info("处理过期优惠券完成，共处理{}张", count);
        } catch (Exception e) {
            log.error("处理过期优惠券异常", e);
        }
    }
} 