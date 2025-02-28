package com.campus.api.mapper;

import com.campus.api.dto.OrderReviewDTO;
import com.campus.api.dto.ReviewListDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderReviewMapper {
    
    @Select("SELECT COUNT(*) FROM order_reviews WHERE order_id = #{orderId} AND order_item_id = #{orderItemId}")
    boolean checkReviewExists(@Param("orderId") Long orderId, @Param("orderItemId") Long orderItemId);
    
    @Insert("INSERT INTO order_reviews (order_id, order_no, order_item_id, user_id, product_id, sku_id, " +
            "rating, content, images, is_anonymous) " +
            "SELECT #{orderId}, o.order_no, #{orderItemId}, #{userId}, oi.product_id, oi.sku_id, " +
            "#{review.rating}, #{review.content}, #{review.images}, #{review.isAnonymous} " +
            "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE o.id = #{orderId} AND oi.id = #{orderItemId}")
    void insertReview(@Param("userId") Long userId, @Param("orderId") Long orderId, 
                     @Param("orderItemId") Long orderItemId, @Param("review") OrderReviewDTO review);
    
    @Update("UPDATE order_reviews SET reply_content = #{replyContent}, reply_time = #{replyTime} " +
            "WHERE id = #{reviewId}")
    void updateReviewReply(@Param("reviewId") Long reviewId, @Param("replyContent") String replyContent, 
                          @Param("replyTime") LocalDateTime replyTime);
    
    @Select("SELECT r.id, r.user_id, u.nickname, u.avatar_url, r.product_id, p.name as product_name, " +
            "p.main_image as product_image, oi.sku_spec_data, r.rating, r.content, r.images, " +
            "r.is_anonymous, r.reply_content, r.reply_time, r.created_at " +
            "FROM order_reviews r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN products p ON r.product_id = p.id " +
            "JOIN order_items oi ON r.order_item_id = oi.id " +
            "WHERE r.product_id = #{productId} " +
            "ORDER BY r.created_at DESC")
    List<ReviewListDTO> selectProductReviews(@Param("productId") Long productId);
    
    @Select("SELECT r.id, r.user_id, u.nickname, u.avatar_url, r.product_id, p.name as product_name, " +
            "p.main_image as product_image, oi.sku_spec_data, r.rating, r.content, r.images, " +
            "r.is_anonymous, r.reply_content, r.reply_time, r.created_at " +
            "FROM order_reviews r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN products p ON r.product_id = p.id " +
            "JOIN order_items oi ON r.order_item_id = oi.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_at DESC")
    List<ReviewListDTO> selectUserReviews(@Param("userId") Long userId);
} 