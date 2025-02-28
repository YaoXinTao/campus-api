package com.campus.api.mapper;

import com.campus.api.entity.ProductFavorite;
import com.campus.api.dto.product.ProductFavoriteDTO;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductFavoriteMapper {
    
    @Insert("INSERT INTO product_favorites(user_id, product_id) VALUES(#{userId}, #{productId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductFavorite favorite);
    
    @Delete("DELETE FROM product_favorites WHERE id = #{id}")
    int deleteById(Long id);
    
    @Delete("DELETE FROM product_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int deleteByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Select("SELECT * FROM product_favorites WHERE id = #{id}")
    ProductFavorite selectById(Long id);
    
    @Select("SELECT * FROM product_favorites WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<ProductFavorite> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    
    @Select("SELECT COUNT(*) FROM product_favorites WHERE user_id = #{userId}")
    Long countByUserId(Long userId);
    
    @Select("SELECT COUNT(*) FROM product_favorites WHERE user_id = #{userId} AND product_id = #{productId}")
    int existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("SELECT f.id, f.product_id, f.created_at, " +
            "p.name, p.main_image, p.price, p.market_price " +
            "FROM product_favorites f " +
            "LEFT JOIN products p ON f.product_id = p.id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.created_at DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<ProductFavoriteDTO> selectFavoriteProducts(@Param("userId") Long userId, 
                                                  @Param("offset") Integer offset, 
                                                  @Param("pageSize") Integer pageSize);
} 