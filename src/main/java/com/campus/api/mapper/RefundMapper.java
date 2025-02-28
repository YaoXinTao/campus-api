package com.campus.api.mapper;

import com.campus.api.dto.RefundApplyDTO;
import com.campus.api.dto.RefundDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RefundMapper {
    
    @Select("SELECT COUNT(*) FROM refund_records WHERE order_id = #{orderId}")
    int countRefundTimes(@Param("orderId") Long orderId);
    
    @Insert("INSERT INTO refund_records (refund_no, order_id, order_no, order_item_id, user_id, " +
            "refund_type, refund_reason_type, refund_reason, refund_amount, refund_quantity, " +
            "is_partial, evidence_images, remark) " +
            "SELECT #{refundNo}, #{orderId}, o.order_no, #{orderItemId}, #{userId}, " +
            "#{refundApply.refundType}, #{refundApply.refundReasonType}, #{refundApply.refundReason}, " +
            "#{refundApply.refundAmount}, #{refundApply.refundQuantity}, " +
            "(CASE WHEN #{refundApply.refundQuantity} < oi.quantity THEN 1 ELSE 0 END), " +
            "#{refundApply.evidenceImages}, #{refundApply.remark} " +
            "FROM orders o JOIN order_items oi ON o.id = oi.order_id " +
            "WHERE o.id = #{orderId} AND oi.id = #{orderItemId}")
    void insertRefund(@Param("refundNo") String refundNo, @Param("userId") Long userId,
                     @Param("orderId") Long orderId, @Param("orderItemId") Long orderItemId,
                     @Param("refundApply") RefundApplyDTO refundApply);
    
    @Insert("INSERT INTO refund_items (refund_id, order_item_id, refund_quantity, refund_amount) " +
            "VALUES ((SELECT id FROM refund_records WHERE refund_no = #{refundNo}), " +
            "#{orderItemId}, #{refundQuantity}, #{refundAmount})")
    void insertRefundItem(@Param("refundNo") String refundNo, @Param("orderItemId") Long orderItemId,
                         @Param("refundQuantity") Integer refundQuantity,
                         @Param("refundAmount") BigDecimal refundAmount);
    
    @Select("SELECT id FROM refund_records WHERE refund_no = #{refundNo}")
    Long getRefundIdByNo(@Param("refundNo") String refundNo);
    
    @Update("UPDATE refund_records SET refund_status = #{status}, reject_reason = #{rejectReason}, " +
            "reject_time = #{rejectTime} WHERE id = #{refundId}")
    void updateRefundStatus(@Param("refundId") Long refundId, @Param("status") Integer status,
                           @Param("rejectReason") String rejectReason,
                           @Param("rejectTime") LocalDateTime rejectTime);
    
    @Update("UPDATE refund_records SET refund_time = #{refundTime} WHERE id = #{refundId}")
    void updateRefundTime(@Param("refundId") Long refundId, @Param("refundTime") LocalDateTime refundTime);
    
    @Select("SELECT r.*, u.nickname, p.name as product_name, p.main_image as product_image, " +
            "oi.sku_spec_data " +
            "FROM refund_records r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN order_items oi ON r.order_item_id = oi.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE r.id = #{refundId}")
    RefundDetailDTO getRefundDetail(@Param("refundId") Long refundId);
    
    @Select("SELECT r.*, u.nickname, p.name as product_name, p.main_image as product_image, " +
            "oi.sku_spec_data " +
            "FROM refund_records r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN order_items oi ON r.order_item_id = oi.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_at DESC")
    List<RefundDetailDTO> selectUserRefunds(@Param("userId") Long userId);
    
    @Select("<script>" +
            "SELECT r.*, u.nickname, p.name as product_name, p.main_image as product_image, " +
            "oi.sku_spec_data " +
            "FROM refund_records r " +
            "JOIN users u ON r.user_id = u.id " +
            "JOIN order_items oi ON r.order_item_id = oi.id " +
            "JOIN products p ON oi.product_id = p.id " +
            "<if test='status != null'> WHERE r.refund_status = #{status} </if>" +
            "ORDER BY r.created_at DESC" +
            "</script>")
    List<RefundDetailDTO> selectMerchantRefunds(@Param("status") Integer status);
} 