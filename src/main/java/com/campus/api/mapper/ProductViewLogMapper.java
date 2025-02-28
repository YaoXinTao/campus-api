package com.campus.api.mapper;

import com.campus.api.entity.ProductViewLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductViewLogMapper {
    
    @Insert("INSERT INTO product_view_logs(user_id, product_id) VALUES(#{userId}, #{productId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductViewLog log);
    
    @Select("SELECT * FROM product_view_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<ProductViewLog> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    
    @Select("SELECT COUNT(*) FROM product_view_logs WHERE user_id = #{userId}")
    Long countByUserId(Long userId);
    
    @Select("SELECT * FROM product_view_logs WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<ProductViewLog> selectByProductId(@Param("productId") Long productId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    
    @Select("SELECT COUNT(*) FROM product_view_logs WHERE product_id = #{productId}")
    Long countByProductId(Long productId);
    
    @Delete("DELETE FROM product_view_logs WHERE user_id = #{userId} AND created_at < #{beforeTime}")
    int deleteByUserIdAndTime(@Param("userId") Long userId, @Param("beforeTime") String beforeTime);
} 