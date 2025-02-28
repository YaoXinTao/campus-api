package com.campus.api.mapper;

import com.campus.api.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 根据ID查询分类
     */
    @Select("SELECT * FROM categories WHERE id = #{id}")
    Category selectById(@Param("id") Long id);

    /**
     * 查询所有分类
     */
    @Select("SELECT * FROM categories ORDER BY sort_order ASC, id DESC")
    List<Category> selectList();

    /**
     * 查询推荐分类
     */
    @Select("SELECT * FROM categories WHERE is_featured = 1 AND status = 1 ORDER BY sort_order ASC, id DESC")
    List<Category> selectFeatured();

    /**
     * 根据父ID查询子分类
     */
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} ORDER BY sort_order ASC, id DESC")
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据层级查询分类
     */
    @Select("SELECT * FROM categories WHERE level = #{level} ORDER BY sort_order ASC, id DESC")
    List<Category> selectByLevel(@Param("level") Integer level);

    /**
     * 插入分类
     */
    @Insert("INSERT INTO categories (parent_id, name, icon_url, banner_url, description, keywords, level, status, is_featured, sort_order, created_by, created_at) " +
            "VALUES (#{parentId}, #{name}, #{iconUrl}, #{bannerUrl}, #{description}, #{keywords}, #{level}, #{status}, #{isFeatured}, #{sortOrder}, #{createdBy}, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);

    /**
     * 更新分类
     */
    @Update({
        "<script>",
        "UPDATE categories",
        "<set>",
        "  <if test='parentId != null'>parent_id = #{parentId},</if>",
        "  <if test='name != null'>name = #{name},</if>",
        "  <if test='iconUrl != null'>icon_url = #{iconUrl},</if>",
        "  <if test='bannerUrl != null'>banner_url = #{bannerUrl},</if>",
        "  <if test='description != null'>description = #{description},</if>",
        "  <if test='keywords != null'>keywords = #{keywords},</if>",
        "  <if test='level != null'>level = #{level},</if>",
        "  <if test='status != null'>status = #{status},</if>",
        "  <if test='isFeatured != null'>is_featured = #{isFeatured},</if>",
        "  <if test='sortOrder != null'>sort_order = #{sortOrder},</if>",
        "  <if test='createdBy != null'>created_by = #{createdBy},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(Category category);

    /**
     * 删除分类
     */
    @Delete("DELETE FROM categories WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 更新分类状态
     */
    @Update("UPDATE categories SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新排序
     */
    @Update("UPDATE categories SET sort_order = #{sortOrder}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateSort(@Param("id") Long id, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新推荐状态
     */
    @Update("UPDATE categories SET is_featured = #{isFeatured}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateFeatured(@Param("id") Long id, @Param("isFeatured") Integer isFeatured);
} 