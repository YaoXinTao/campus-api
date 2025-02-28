package com.campus.api.mapper;

import com.campus.api.entity.ProductTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface ProductTagRelationMapper {
    
    @Select("SELECT tag_id FROM product_tag_relations WHERE product_id = #{productId}")
    List<Long> selectTagIdsByProductId(Long productId);
    
    @Select("SELECT product_id FROM product_tag_relations WHERE tag_id = #{tagId}")
    List<Long> selectProductIdsByTagId(Long tagId);
    
    @Insert("INSERT INTO product_tag_relations(product_id, tag_id) VALUES(#{productId}, #{tagId})")
    int insert(ProductTagRelation relation);
    
    @Delete("DELETE FROM product_tag_relations WHERE tag_id = #{tagId}")
    int deleteByTagId(Long tagId);
    
    @Delete("DELETE FROM product_tag_relations WHERE product_id = #{productId}")
    int deleteByProductId(Long productId);
} 