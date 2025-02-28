package com.campus.api.mapper;

import com.campus.api.dto.stats.CategoryStatsDTO;
import com.campus.api.dto.stats.HotProductDTO;
import com.campus.api.entity.Product;
import com.campus.api.dto.product.ProductQuery;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

@Mapper
public interface ProductMapper {
    
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product selectById(@Param("id") Long id);
    
    @Select({"<script>",
            "SELECT p.id, p.category_id, p.name, p.brief, p.keywords, p.main_image, ",
            "p.album, p.unit, p.price, p.market_price, p.status, p.verify_status, ",
            "p.is_featured, p.sort_order, p.view_count, p.created_by, p.created_at, p.updated_at, ",
            "COALESCE(SUM(s.stock), 0) as total_stock, ",
            "COALESCE(SUM(s.sales), 0) as total_sales " +
            "FROM products p ",
            "LEFT JOIN product_skus s ON p.id = s.product_id ",
            "WHERE 1=1 ",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (p.name LIKE CONCAT('%', #{keyword}, '%') ",
            "OR p.brief LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "<if test='categoryId != null'>",
            "AND p.category_id = #{categoryId} ",
            "</if>",
            "<if test='status != null'>",
            "AND p.status = #{status} ",
            "</if>",
            "<if test='verifyStatus != null'>",
            "AND p.verify_status = #{verifyStatus} ",
            "</if>",
            "<if test='isFeatured != null'>",
            "AND p.is_featured = #{isFeatured} ",
            "</if>",
            "<if test='minPrice != null'>",
            "AND p.price >= #{minPrice} ",
            "</if>",
            "<if test='maxPrice != null'>",
            "AND p.price &lt;= #{maxPrice} ",
            "</if>",
            "GROUP BY p.id, p.category_id, p.name, p.brief, p.keywords, p.main_image, ",
            "p.album, p.unit, p.price, p.market_price, p.status, p.verify_status, ",
            "p.is_featured, p.sort_order, p.view_count, p.created_by, p.created_at, p.updated_at ",
            "ORDER BY ",
            "<choose>",
            "  <when test='sortField == \"sales\"'>total_sales</when>",
            "  <when test='sortField == \"price\"'>p.price</when>",
            "  <otherwise>p.created_at</otherwise>",
            "</choose> ${sortOrder} ",
            "LIMIT #{limit} OFFSET #{offset}",
            "</script>"})
    List<Product> selectList(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("status") Integer status,
                           @Param("verifyStatus") Integer verifyStatus,
                           @Param("isFeatured") Integer isFeatured,
                           @Param("sortField") String sortField,
                           @Param("sortOrder") String sortOrder,
                           @Param("minPrice") Integer minPrice,
                           @Param("maxPrice") Integer maxPrice,
                           @Param("limit") Integer limit,
                           @Param("offset") Integer offset);

    @SelectProvider(type = ProductSqlProvider.class, method = "count")
    long count(@Param("keyword") String keyword,
              @Param("categoryId") Long categoryId,
              @Param("status") Integer status,
              @Param("verifyStatus") Integer verifyStatus,
              @Param("isFeatured") Integer isFeatured);

    @Select("SELECT p.*, " +
            "COALESCE(SUM(s.stock), 0) as total_stock, " +
            "COALESCE(SUM(s.sales), 0) as total_sales " +
            "FROM products p " +
            "LEFT JOIN product_skus s ON p.id = s.product_id " +
            "WHERE p.id = #{id} " +
            "GROUP BY p.id")
    Product selectByIdWithStats(@Param("id") Long id);

    @Insert("INSERT INTO products (category_id, name, brief, keywords, main_image, album, unit, " +
            "price, market_price, total_stock, total_sales, view_count, status, " +
            "verify_status, is_featured, sort_order, created_by) " +
            "VALUES (#{categoryId}, #{name}, #{brief}, #{keywords}, #{mainImage}, #{album}, #{unit}, " +
            "#{price}, #{marketPrice}, #{totalStock}, #{totalSales}, #{viewCount}, #{status}, " +
            "#{verifyStatus}, #{isFeatured}, #{sortOrder}, #{createdBy})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    @UpdateProvider(type = ProductSqlProvider.class, method = "update")
    int update(Product product);

    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Update("UPDATE products SET status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE products SET verify_status = #{verifyStatus}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateVerifyStatus(@Param("id") Long id, @Param("verifyStatus") Integer verifyStatus);

    @Update("UPDATE products SET is_featured = #{isFeatured}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateFeatured(@Param("id") Long id, @Param("isFeatured") Integer isFeatured);

    @Update("UPDATE products SET view_count = view_count + 1, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateViewCount(@Param("id") Long id);

    @Update("UPDATE products SET total_sales = total_sales + #{quantity}, total_stock = total_stock - #{quantity}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int incrementSales(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Select("SELECT * FROM products WHERE is_featured = 1 AND status = 1 " +
            "ORDER BY sort_order ASC, id DESC LIMIT #{limit}")
    List<Product> selectFeatured(@Param("limit") Integer limit);

    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 1 " +
            "ORDER BY sort_order ASC, id DESC" +
            "<if test='limit != null'> LIMIT #{limit}</if>")
    List<Product> selectByCategory(@Param("categoryId") Long categoryId, @Param("limit") Integer limit);

    @SelectProvider(type = ProductSqlProvider.class, method = "searchProducts")
    List<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("productIds") List<Long> productIds,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("onlyInStock") Boolean onlyInStock,
            @Param("featured") Boolean featured,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") int offset,
            @Param("size") int size
    );

    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND id != #{productId} AND status = 1 " +
            "ORDER BY total_sales DESC LIMIT #{offset}, #{size}")
    List<Product> selectSimilarProducts(
            @Param("categoryId") Long categoryId,
            @Param("productId") Long productId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * 更新商品的库存和销量
     */
    @Update("UPDATE products SET total_stock = #{totalStock}, total_sales = #{totalSales}, updated_at = CURRENT_TIMESTAMP WHERE id = #{productId}")
    int updateStockAndSales(
        @Param("productId") Long productId,
        @Param("totalStock") Integer totalStock,
        @Param("totalSales") Integer totalSales
    );

    /**
     * 获取每日统计数据
     */
    @Select("SELECT " +
            "COUNT(*) as totalProducts, " +
            "COALESCE(SUM(total_sales), 0) as totalSales, " +
            "(SELECT COUNT(*) FROM product_reviews WHERE DATE(created_at) <= #{date}) as totalComments, " +
            "COALESCE((SELECT AVG(rating) FROM product_reviews WHERE DATE(created_at) <= #{date}), 0) as averageRating " +
            "FROM products " +
            "WHERE DATE(created_at) <= #{date}")
    Map<String, Object> selectDailyStats(@Param("date") LocalDate date);

    /**
     * 获取日期范围内的每日销量
     */
    @Select("WITH RECURSIVE dates AS (" +
            "  SELECT #{startDate} as date " +
            "  UNION ALL " +
            "  SELECT DATE_ADD(date, INTERVAL 1 DAY) " +
            "  FROM dates " +
            "  WHERE date < #{endDate}" +
            "), daily_sales AS (" +
            "  SELECT DATE(created_at) as date, SUM(total_sales) as sales " +
            "  FROM products " +
            "  WHERE DATE(created_at) BETWEEN #{startDate} AND #{endDate} " +
            "  GROUP BY DATE(created_at)" +
            ")" +
            "SELECT d.date, COALESCE(ds.sales, 0) as sales " +
            "FROM dates d " +
            "LEFT JOIN daily_sales ds ON d.date = ds.date " +
            "ORDER BY d.date")
    List<Map<String, Object>> selectDailySales(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * 获取分类统计数据
     */
    @Select("SELECT c.name, COUNT(p.id) as value " +
            "FROM categories c " +
            "LEFT JOIN products p ON p.category_id = c.id " +
            "GROUP BY c.id, c.name " +
            "ORDER BY value DESC")
    List<CategoryStatsDTO> selectCategoryStats();

    /**
     * 获取热销商品
     */
    @Select("SELECT " +
            "p.id, p.name, p.brief, p.main_image as mainImage, " +
            "c.name as categoryName, p.price, p.total_sales as sales, " +
            "COALESCE((SELECT AVG(rating) FROM product_reviews WHERE product_id = p.id), 0) as rating " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "WHERE p.status = 1 " +
            "ORDER BY p.total_sales DESC, rating DESC " +
            "LIMIT #{limit}")
    List<HotProductDTO> selectHotProducts(@Param("limit") Integer limit);
}