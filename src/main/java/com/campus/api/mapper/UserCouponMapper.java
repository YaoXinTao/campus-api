package com.campus.api.mapper;

import com.campus.api.entity.UserCoupon;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserCouponMapper {
    
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserCoupon> selectByUserId(Long userId);
    
    @Select("SELECT * FROM user_coupons WHERE id = #{id} AND user_id = #{userId}")
    UserCoupon selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM user_coupons WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    int countUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
    
    @Insert("INSERT INTO user_coupons(user_id, coupon_id, status, receive_time) " +
            "VALUES(#{userId}, #{couponId}, #{status}, #{receiveTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);
    
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND status = 1 " +
            "AND coupon_id IN (SELECT id FROM coupons WHERE NOW() BETWEEN start_time AND end_time)")
    List<UserCoupon> selectAvailable(Long userId);
    
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND status = #{status}")
    List<UserCoupon> selectByStatus(@Param("userId") Long userId, @Param("status") Integer status);
    
    @Update("UPDATE user_coupons SET status = 3 " +
            "WHERE status = 1 " +
            "AND coupon_id IN (SELECT id FROM coupons WHERE NOW() > end_time)")
    int updateExpiredCoupons();

    @Select("SELECT * FROM user_coupons WHERE coupon_id = #{couponId} AND user_id = #{userId} AND status = 1 ORDER BY created_at DESC LIMIT 1")
    UserCoupon selectByCouponIdAndUserId(@Param("couponId") Long couponId, @Param("userId") Long userId);

    /**
     * 使用乐观锁更新优惠券状态
     */
    @Update("UPDATE user_coupons SET status = #{status}, use_time = #{useTime}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND status = 1 AND version = #{version}")
    int updateStatusWithVersion(@Param("id") Long id,
                              @Param("status") Integer status,
                              @Param("useTime") LocalDateTime useTime,
                              @Param("version") Integer version);

    /**
     * 更新优惠券关联的订单ID
     */
    @Update("UPDATE user_coupons SET order_id = #{orderId}, updated_at = NOW() " +
            "WHERE id = #{id} AND status = 2")
    int updateOrderId(@Param("id") Long id, @Param("orderId") Long orderId);

    /**
     * 取消订单时更新优惠券状态
     */
    @Update("UPDATE user_coupons SET status = #{status}, use_time = null, order_id = null, updated_at = NOW() " +
            "WHERE id = #{id} AND status = 2")
    int updateStatusForCancel(@Param("id") Long id, @Param("status") Integer status);
} 