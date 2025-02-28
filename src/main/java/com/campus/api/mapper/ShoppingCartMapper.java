package com.campus.api.mapper;

import com.campus.api.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    
    @Insert("INSERT INTO shopping_cart(user_id, product_id, sku_id, quantity, selected) " +
            "VALUES(#{userId}, #{productId}, #{skuId}, #{quantity}, #{selected})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ShoppingCart cart);
    
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND sku_id = #{skuId}")
    ShoppingCart findByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);
    
    @Select("SELECT * FROM shopping_cart WHERE id = #{id}")
    ShoppingCart findById(Long id);
    
    @Update("UPDATE shopping_cart SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    @Update("UPDATE shopping_cart SET selected = #{selected} WHERE id = #{id}")
    int updateSelected(@Param("id") Long id, @Param("selected") Integer selected);
    
    @Update("UPDATE shopping_cart SET " +
            "sku_id = #{skuId}, " +
            "quantity = #{quantity} " +
            "WHERE id = #{id}")
    int updateCartItem(ShoppingCart cart);
    
    @Delete("DELETE FROM shopping_cart WHERE id = #{id}")
    int deleteById(Long id);
    
    @Select("SELECT " +
            "sc.*, " +
            "p.name as product_name, " +
            "p.main_image as product_image, " +
            "ps.sku_code as sku_name, " +
            "ps.image_url as sku_image, " +
            "JSON_UNQUOTE(ps.spec_data) as sku_spec, " +
            "COALESCE(ps.price, p.price) as sku_price " +
            "FROM shopping_cart sc " +
            "LEFT JOIN products p ON sc.product_id = p.id " +
            "LEFT JOIN product_skus ps ON sc.sku_id = ps.id " +
            "WHERE sc.user_id = #{userId} " +
            "ORDER BY sc.created_at DESC")
    List<ShoppingCart> findByUserId(@Param("userId") Long userId);
    
    @Update("UPDATE shopping_cart SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantityById(@Param("id") Long id, @Param("quantity") Integer quantity);
} 