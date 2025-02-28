package com.campus.api.mapper;

import com.campus.api.entity.Coupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CouponMapper {
    
    @Select("<script>" +
            "SELECT * FROM coupons " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND name LIKE CONCAT('%',#{keyword},'%')" +
            "</if>" +
            "<if test='type != null'>" +
            "   AND type = #{type}" +
            "</if>" +
            "<if test='status != null'>" +
            "   AND status = #{status}" +
            "</if>" +
            "</where>" +
            "ORDER BY created_at DESC " +
            "LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Coupon> selectList(@Param("keyword") String keyword,
                           @Param("type") Integer type,
                           @Param("status") Integer status,
                           @Param("offset") Integer offset,
                           @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM coupons " +
            "<where>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "   AND name LIKE CONCAT('%',#{keyword},'%')" +
            "</if>" +
            "<if test='type != null'>" +
            "   AND type = #{type}" +
            "</if>" +
            "<if test='status != null'>" +
            "   AND status = #{status}" +
            "</if>" +
            "</where>" +
            "</script>")
    long selectCount(@Param("keyword") String keyword,
                    @Param("type") Integer type,
                    @Param("status") Integer status);

    @Select("SELECT * FROM coupons WHERE id = #{id}")
    Coupon selectById(Long id);

    @Insert("INSERT INTO coupons(name, type, amount, min_spend, category_id, " +
            "start_time, end_time, total_count, remain_count, per_limit, " +
            "description, status, created_by) " +
            "VALUES(#{name}, #{type}, #{amount}, #{minSpend}, #{categoryId}, " +
            "#{startTime}, #{endTime}, #{totalCount}, #{totalCount}, #{perLimit}, " +
            "#{description}, #{status}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon coupon);

    @Update("UPDATE coupons SET name = #{name}, type = #{type}, amount = #{amount}, " +
            "min_spend = #{minSpend}, category_id = #{categoryId}, start_time = #{startTime}, " +
            "end_time = #{endTime}, total_count = #{totalCount}, remain_count = #{remainCount}, " +
            "per_limit = #{perLimit}, description = #{description}, status = #{status} " +
            "WHERE id = #{id}")
    int update(Coupon coupon);

    @Update("UPDATE coupons SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE coupons SET remain_count = remain_count - 1 WHERE id = #{id} AND remain_count > 0")
    int decrementRemainCount(Long id);

    @Select("SELECT * FROM coupons WHERE status = 1 " +
            "AND NOW() <= end_time " +
            "AND remain_count > 0 " +
            "ORDER BY created_at DESC")
    List<Coupon> selectAvailable();

    @Select("SELECT * FROM coupons " +
            "WHERE status = 1 " +
            "AND NOW() BETWEEN start_time AND end_time " +
            "AND remain_count > 0 " +
            "ORDER BY created_at DESC")
    List<Coupon> selectActiveOnly();

    @Select("SELECT * FROM coupons " +
            "WHERE NOW() BETWEEN start_time AND end_time " +
            "ORDER BY created_at DESC")
    List<Coupon> selectValidTimeOnly();

    @Select("SELECT * FROM coupons " +
            "WHERE remain_count > 0 " +
            "ORDER BY created_at DESC")
    List<Coupon> selectHasRemainOnly();
} 