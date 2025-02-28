package com.campus.api.mapper;

import com.campus.api.entity.ProductSku;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductSkuMapper {
    
    @Select("SELECT * FROM product_skus WHERE id = #{id}")
    ProductSku selectById(@Param("id") Long id);

    @Select("SELECT * FROM product_skus WHERE product_id = #{productId} ORDER BY id ASC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuCode", column = "sku_code"),
        @Result(property = "specData", column = "spec_data"),
        @Result(property = "price", column = "price"),
        @Result(property = "marketPrice", column = "market_price"),
        @Result(property = "costPrice", column = "cost_price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "sales", column = "sales"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "status", column = "status")
    })
    List<ProductSku> selectByProductId(@Param("productId") Long productId);

    @Insert("INSERT INTO product_skus (product_id, sku_code, spec_data, price, market_price, " +
            "cost_price, stock, sales, image_url, status, created_at, updated_at) " +
            "VALUES (#{productId}, #{skuCode}, #{specData}, #{price}, #{marketPrice}, " +
            "#{costPrice}, #{stock}, #{sales}, #{imageUrl}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSku sku);

    @Update({
        "<script>",
        "UPDATE product_skus",
        "<set>",
        "  <if test='skuCode != null'>sku_code = #{skuCode},</if>",
        "  <if test='specData != null'>spec_data = #{specData},</if>",
        "  <if test='price != null'>price = #{price},</if>",
        "  <if test='marketPrice != null'>market_price = #{marketPrice},</if>",
        "  <if test='costPrice != null'>cost_price = #{costPrice},</if>",
        "  <if test='stock != null'>stock = #{stock},</if>",
        "  <if test='sales != null'>sales = #{sales},</if>",
        "  <if test='imageUrl != null'>image_url = #{imageUrl},</if>",
        "  <if test='status != null'>status = #{status},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(ProductSku sku);

    @Delete("DELETE FROM product_skus WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    @Update("UPDATE product_skus SET stock = #{stock}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock);

    @Update("UPDATE product_skus SET sales = #{sales}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateSales(@Param("id") Long id, @Param("sales") Integer sales);

    @Delete("DELETE FROM product_skus WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COALESCE(SUM(stock), 0) FROM product_skus WHERE product_id = #{productId}")
    Integer sumStockByProductId(@Param("productId") Long productId);

    @Select("SELECT COALESCE(SUM(sales), 0) FROM product_skus WHERE product_id = #{productId}")
    Integer sumSalesByProductId(@Param("productId") Long productId);

    @Update("UPDATE product_skus SET stock = stock - #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id} AND stock >= #{quantity}")
    int lockStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE product_skus SET stock = stock + #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int releaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE product_skus SET stock = stock + #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Update("UPDATE product_skus SET stock = stock - #{quantity}, sales = sales + #{quantity}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int confirmLockedStock(@Param("id") Long id, @Param("quantity") Integer quantity);
} 