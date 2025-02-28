package com.campus.api.mapper;

import com.campus.api.entity.Product;
import com.campus.api.dto.product.ProductQuery;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import java.util.List;

public class ProductSqlProvider {
    public String selectList(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("verifyStatus") Integer verifyStatus,
            @Param("isFeatured") Integer isFeatured,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("limit") Integer limit,
            @Param("offset") Integer offset) {
        SQL sql = new SQL();
        sql.SELECT("p.*, COALESCE(SUM(s.stock), 0) as total_stock, COALESCE(SUM(s.sales), 0) as total_sales")
           .FROM("products p")
           .LEFT_OUTER_JOIN("product_skus s ON p.id = s.product_id");
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.WHERE("(p.name LIKE CONCAT('%', #{keyword}, '%') OR p.brief LIKE CONCAT('%', #{keyword}, '%') OR p.keywords LIKE CONCAT('%', #{keyword}, '%'))");
        }
        if (categoryId != null) {
            sql.WHERE("p.category_id = #{categoryId}");
        }
        if (status != null) {
            sql.WHERE("p.status = #{status}");
        }
        if (verifyStatus != null) {
            sql.WHERE("p.verify_status = #{verifyStatus}");
        }
        if (isFeatured != null) {
            sql.WHERE("p.is_featured = #{isFeatured}");
        }
        
        sql.GROUP_BY("p.id");
        
        if (sortField != null && !sortField.isEmpty()) {
            String direction = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            sql.ORDER_BY(sortField + " " + direction);
        } else {
            sql.ORDER_BY("p.sort_order ASC, p.id DESC");
        }
        
        String baseSql = sql.toString();
        if (limit != null && offset != null) {
            baseSql += " LIMIT #{limit} OFFSET #{offset}";
        }
        return baseSql;
    }

    public String count(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") Integer status,
            @Param("verifyStatus") Integer verifyStatus,
            @Param("isFeatured") Integer isFeatured) {
        SQL sql = new SQL();
        sql.SELECT("COUNT(*)")
           .FROM("products");
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.WHERE("(name LIKE CONCAT('%', #{keyword}, '%') OR brief LIKE CONCAT('%', #{keyword}, '%') OR keywords LIKE CONCAT('%', #{keyword}, '%'))");
        }
        if (categoryId != null) {
            sql.WHERE("category_id = #{categoryId}");
        }
        if (status != null) {
            sql.WHERE("status = #{status}");
        }
        if (verifyStatus != null) {
            sql.WHERE("verify_status = #{verifyStatus}");
        }
        if (isFeatured != null) {
            sql.WHERE("is_featured = #{isFeatured}");
        }
        return sql.toString();
    }

    public String update(final Product product) {
        SQL sql = new SQL();
        sql.UPDATE("products");
        
        if (product.getCategoryId() != null) sql.SET("category_id = #{categoryId}");
        if (product.getName() != null) sql.SET("name = #{name}");
        if (product.getBrief() != null) sql.SET("brief = #{brief}");
        if (product.getKeywords() != null) sql.SET("keywords = #{keywords}");
        if (product.getMainImage() != null) sql.SET("main_image = #{mainImage}");
        if (product.getAlbum() != null) sql.SET("album = #{album}");
        if (product.getUnit() != null) sql.SET("unit = #{unit}");
        if (product.getPrice() != null) sql.SET("price = #{price}");
        if (product.getMarketPrice() != null) sql.SET("market_price = #{marketPrice}");
        if (product.getTotalStock() != null) sql.SET("total_stock = #{totalStock}");
        if (product.getTotalSales() != null) sql.SET("total_sales = #{totalSales}");
        if (product.getViewCount() != null) sql.SET("view_count = #{viewCount}");
        if (product.getStatus() != null) sql.SET("status = #{status}");
        if (product.getVerifyStatus() != null) sql.SET("verify_status = #{verifyStatus}");
        if (product.getIsFeatured() != null) sql.SET("is_featured = #{isFeatured}");
        if (product.getSortOrder() != null) sql.SET("sort_order = #{sortOrder}");
        sql.SET("updated_at = CURRENT_TIMESTAMP");
        sql.WHERE("id = #{id}");
        
        return sql.toString();
    }

    public String searchProducts(
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
            @Param("size") int size) {
        
        SQL sql = new SQL();
        sql.SELECT("*")
           .FROM("products")
           .WHERE("status = 1");

        if (keyword != null && !keyword.isEmpty()) {
            sql.WHERE("(name LIKE CONCAT('%', #{keyword}, '%') OR brief LIKE CONCAT('%', #{keyword}, '%') OR keywords LIKE CONCAT('%', #{keyword}, '%'))");
        }
        if (categoryId != null) {
            sql.WHERE("category_id = #{categoryId}");
        }
        if (productIds != null && !productIds.isEmpty()) {
            sql.WHERE("id IN <foreach item='id' collection='productIds' open='(' separator=',' close=')'>#{id}</foreach>");
        }
        if (minPrice != null) {
            sql.WHERE("price >= #{minPrice}");
        }
        if (maxPrice != null) {
            sql.WHERE("price <= #{maxPrice}");
        }
        if (onlyInStock != null && onlyInStock) {
            sql.WHERE("total_stock > 0");
        }
        if (featured != null && featured) {
            sql.WHERE("is_featured = 1");
        }

        String orderBy;
        if ("price".equals(sortField)) {
            orderBy = "price " + ("asc".equals(sortOrder) ? "ASC" : "DESC");
        } else if ("sales".equals(sortField)) {
            orderBy = "total_sales " + ("asc".equals(sortOrder) ? "ASC" : "DESC");
        } else if ("views".equals(sortField)) {
            orderBy = "view_count " + ("asc".equals(sortOrder) ? "ASC" : "DESC");
        } else {
            orderBy = "created_at DESC";
        }
        sql.ORDER_BY(orderBy);

        String baseSql = sql.toString();
        return baseSql + " LIMIT #{size} OFFSET #{offset}";
    }
} 