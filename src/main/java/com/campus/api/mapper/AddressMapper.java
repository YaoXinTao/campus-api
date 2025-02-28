package com.campus.api.mapper;

import com.campus.api.entity.Address;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressMapper {
    
    @Select("SELECT * FROM addresses WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<Address> findByUserId(Long userId);
    
    @Select("SELECT * FROM addresses WHERE id = #{id} AND user_id = #{userId}")
    Address findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Insert("INSERT INTO addresses(user_id, receiver, phone, province, city, district, " +
            "detail_address, postal_code, is_default, tag) " +
            "VALUES(#{userId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, " +
            "#{detailAddress}, #{postalCode}, #{isDefault}, #{tag})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Address address);
    
    @Update("UPDATE addresses SET receiver = #{receiver}, phone = #{phone}, " +
            "province = #{province}, city = #{city}, district = #{district}, " +
            "detail_address = #{detailAddress}, postal_code = #{postalCode}, " +
            "is_default = #{isDefault}, tag = #{tag} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int update(Address address);
    
    @Delete("DELETE FROM addresses WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
    
    @Update("UPDATE addresses SET is_default = 0 WHERE user_id = #{userId} AND id != #{id}")
    int clearOtherDefault(@Param("userId") Long userId, @Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM addresses WHERE user_id = #{userId}")
    int countByUserId(Long userId);
    
    @Select("SELECT * FROM addresses WHERE id = #{id}")
    Address selectById(@Param("id") Long id);
} 