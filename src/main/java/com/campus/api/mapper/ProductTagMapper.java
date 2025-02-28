package com.campus.api.mapper;

import com.campus.api.entity.ProductTag;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductTagMapper {

    @Select("SELECT COUNT(*) FROM product_tags WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    Long count(@Param("keyword") String keyword);

    @Select("""
        SELECT * FROM product_tags 
        WHERE name LIKE CONCAT('%', #{keyword}, '%')
        ORDER BY sort_order ASC, id DESC
        LIMIT #{offset}, #{pageSize}
    """)
    List<ProductTag> selectList(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT * FROM product_tags WHERE id = #{id}")
    ProductTag selectById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM product_tags WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    @Select("SELECT COUNT(*) FROM product_tags WHERE name = #{name} AND id != #{id}")
    boolean existsByNameAndNotId(@Param("name") String name, @Param("id") Long id);

    @Insert("""
        INSERT INTO product_tags(name, icon, color, status, sort_order)
        VALUES(#{name}, #{icon}, #{color}, #{status}, #{sortOrder})
    """)
    void insert(ProductTag tag);

    @Update("""
        UPDATE product_tags 
        SET name = #{name}, icon = #{icon}, color = #{color}, 
            status = #{status}, sort_order = #{sortOrder}
        WHERE id = #{id}
    """)
    void update(ProductTag tag);

    @Delete("DELETE FROM product_tags WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    @Update("UPDATE product_tags SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM product_tag_relations WHERE tag_id = #{tagId}")
    int countProductRelations(@Param("tagId") Long tagId);

    @Delete("DELETE FROM product_tag_relations WHERE tag_id = #{tagId}")
    void deleteProductRelations(@Param("tagId") Long tagId);

    @Insert("""
        <script>
        INSERT INTO product_tag_relations(tag_id, product_id) VALUES
        <foreach collection="productIds" item="productId" separator=",">
            (#{tagId}, #{productId})
        </foreach>
        </script>
    """)
    void batchInsertProductRelations(@Param("tagId") Long tagId, @Param("productIds") List<Long> productIds);

    @Select("SELECT * FROM product_tags WHERE status = 1 ORDER BY sort_order ASC")
    List<ProductTag> selectAllEnabled();

    @Select("""
        SELECT t.* FROM product_tags t 
        INNER JOIN product_tag_relations r ON t.id = r.tag_id 
        WHERE r.product_id = #{productId} AND t.status = 1 
        ORDER BY t.sort_order ASC
    """)
    List<ProductTag> selectByProductId(@Param("productId") Long productId);
} 