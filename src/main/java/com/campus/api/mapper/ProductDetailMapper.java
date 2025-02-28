package com.campus.api.mapper;

import com.campus.api.entity.ProductDetail;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductDetailMapper {
    /**
     * 根据商品ID查询详情
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId}")
    ProductDetail selectByProductId(@Param("productId") Long productId);

    /**
     * 插入商品详情
     */
    @Insert("INSERT INTO product_details (product_id, description, rich_content, spec_desc, " +
            "packing_list, service_notes) " +
            "VALUES (#{productId}, #{description}, #{richContent}, #{specDesc}, " +
            "#{packingList}, #{serviceNotes})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductDetail detail);

    /**
     * 更新商品详情
     */
    @Update({
        "<script>",
        "UPDATE product_details",
        "<set>",
        "  <if test='description != null'>description = #{description},</if>",
        "  <if test='richContent != null'>rich_content = #{richContent},</if>",
        "  <if test='specDesc != null'>spec_desc = #{specDesc},</if>",
        "  <if test='packingList != null'>packing_list = #{packingList},</if>",
        "  <if test='serviceNotes != null'>service_notes = #{serviceNotes},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE product_id = #{productId}",
        "</script>"
    })
    int update(ProductDetail detail);

    /**
     * 根据商品ID删除详情
     */
    @Delete("DELETE FROM product_details WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
} 