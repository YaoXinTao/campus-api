package com.campus.api.mapper;

import com.campus.api.entity.ProductSpecTemplate;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductSpecTemplateMapper {
    /**
     * 根据ID查询规格模板
     */
    @Select("SELECT * FROM product_spec_templates WHERE id = #{id}")
    ProductSpecTemplate selectById(@Param("id") Long id);

    /**
     * 查询规格模板列表
     */
    @Select("SELECT * FROM product_spec_templates ORDER BY id DESC")
    List<ProductSpecTemplate> selectList();

    /**
     * 新增规格模板
     */
    @Insert("INSERT INTO product_spec_templates (name, spec_items, status) " +
            "VALUES (#{name}, #{specItems}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSpecTemplate template);

    /**
     * 更新规格模板
     */
    @Update({
        "<script>",
        "UPDATE product_spec_templates",
        "<set>",
        "  <if test='name != null'>name = #{name},</if>",
        "  <if test='specItems != null'>spec_items = #{specItems},</if>",
        "  <if test='status != null'>status = #{status},</if>",
        "  updated_at = CURRENT_TIMESTAMP",
        "</set>",
        "WHERE id = #{id}",
        "</script>"
    })
    int update(ProductSpecTemplate template);

    /**
     * 删除规格模板
     */
    @Delete("DELETE FROM product_spec_templates WHERE id = #{id}")
    int delete(@Param("id") Long id);

    /**
     * 更新规格模板状态
     */
    @Update("UPDATE product_spec_templates SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 