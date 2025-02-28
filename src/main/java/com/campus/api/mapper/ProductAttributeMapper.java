package com.campus.api.mapper;

import com.campus.api.entity.ProductAttribute;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductAttributeMapper {
    
    @Insert("INSERT INTO product_attributes(product_id, name, value, sort_order) VALUES(#{productId}, #{name}, #{value}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductAttribute attribute);
    
    @Update("UPDATE product_attributes SET name=#{name}, value=#{value}, sort_order=#{sortOrder} WHERE id=#{id}")
    int update(ProductAttribute attribute);
    
    @Delete("DELETE FROM product_attributes WHERE id=#{id}")
    int deleteById(Long id);
    
    @Select("SELECT * FROM product_attributes WHERE id=#{id}")
    ProductAttribute selectById(Long id);
    
    @Select("SELECT * FROM product_attributes WHERE product_id=#{productId} ORDER BY sort_order")
    List<ProductAttribute> selectByProductId(Long productId);
    
    @Delete("DELETE FROM product_attributes WHERE product_id=#{productId}")
    int deleteByProductId(Long productId);
} 