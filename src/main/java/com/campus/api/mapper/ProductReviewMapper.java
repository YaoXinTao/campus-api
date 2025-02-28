package com.campus.api.mapper;

import com.campus.api.entity.ProductReview;
import com.campus.api.dto.product.ProductReviewQuery;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductReviewMapper {

    @Insert("INSERT INTO product_reviews(user_id, product_id, order_id, sku_id, rating, content, images, status) " +
            "VALUES(#{userId}, #{productId}, #{orderId}, #{skuId}, #{rating}, #{content}, #{images}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductReview review);

    @Select({
        "<script>",
        "SELECT r.*, ",
        "p.name as product_name, p.main_image as product_image, ",
        "u.nickname as user_name, u.avatar_url as user_avatar, u.phone as user_phone ",
        "FROM product_reviews r ",
        "LEFT JOIN products p ON r.product_id = p.id ",
        "LEFT JOIN users u ON r.user_id = u.id ",
        "WHERE 1=1 ",
        "<if test='keyword != null and keyword != \"\"'>",
        "AND (p.name LIKE CONCAT('%', #{keyword}, '%') ",
        "OR u.nickname LIKE CONCAT('%', #{keyword}, '%') ",
        "OR r.content LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "<if test='rating != null'>",
        "AND r.rating = #{rating} ",
        "</if>",
        "<if test='status != null'>",
        "AND r.status = #{status} ",
        "</if>",
        "ORDER BY r.created_at DESC",
        "</script>"
    })
    List<ProductReview> selectList(ProductReviewQuery query);

    @Select("SELECT * FROM product_reviews WHERE id = #{id}")
    ProductReview selectById(@Param("id") Long id);

    @Update("UPDATE product_reviews SET reply = #{reply}, reply_time = #{replyTime}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateById(ProductReview review);

    @Delete("DELETE FROM product_reviews WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update({
        "<script>",
        "UPDATE product_reviews SET status = #{status}, updated_at = CURRENT_TIMESTAMP ",
        "WHERE id IN ",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    @Delete({
        "<script>",
        "DELETE FROM product_reviews WHERE id IN ",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
        "#{id}",
        "</foreach>",
        "</script>"
    })
    int batchDelete(@Param("ids") List<Long> ids);

    @Select("SELECT r.*, u.nickname as user_name, u.avatar_url as user_avatar, " +
            "DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s') as created_at, " +
            "DATE_FORMAT(r.reply_time, '%Y-%m-%d %H:%i:%s') as reply_time " +
            "FROM product_reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} " +
            "ORDER BY r.created_at DESC LIMIT #{offset}, #{pageSize}")
    List<ProductReview> selectByProductId(@Param("productId") Long productId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM product_reviews WHERE product_id = #{productId}")
    Long countByProductId(@Param("productId") Long productId);

    @Select("SELECT * FROM product_reviews WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<ProductReview> selectByUserId(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM product_reviews WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);

    @Update("UPDATE product_reviews SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM product_reviews WHERE order_id = #{orderId} AND sku_id = #{skuId}")
    int existsByOrderIdAndSkuId(@Param("orderId") Long orderId, @Param("skuId") Long skuId);

    /**
     * 获取评分分布数据
     * 返回一个包含5个元素的列表，分别代表1-5星的评价数量
     */
    @Select("WITH RECURSIVE ratings AS (" +
            "  SELECT 1 as rating_level UNION ALL" +
            "  SELECT rating_level + 1 FROM ratings WHERE rating_level < 5" +
            "), review_counts AS (" +
            "  SELECT " +
            "    CASE" +
            "      WHEN rating > 0 AND rating <= 1 THEN 1" +
            "      WHEN rating > 1 AND rating <= 2 THEN 2" +
            "      WHEN rating > 2 AND rating <= 3 THEN 3" +
            "      WHEN rating > 3 AND rating <= 4 THEN 4" +
            "      WHEN rating > 4 AND rating <= 5 THEN 5" +
            "    END as rating_level," +
            "    COUNT(*) as count" +
            "  FROM product_reviews" +
            "  WHERE status = 1" +
            "  GROUP BY rating_level" +
            ")" +
            "SELECT COALESCE(rc.count, 0) as count " +
            "FROM ratings r " +
            "LEFT JOIN review_counts rc ON r.rating_level = rc.rating_level " +
            "ORDER BY r.rating_level")
    List<Integer> selectRatingDistribution();

    /**
     * 获取商品评分统计
     */
    @Select("SELECT rating, COUNT(*) as count " +
            "FROM product_reviews " +
            "WHERE product_id = #{productId} AND status = 1 " +
            "GROUP BY rating " +
            "ORDER BY rating DESC")
    List<Map<String, Object>> selectRatingStats(@Param("productId") Long productId);
} 