package com.campus.api.mapper;

import com.campus.api.entity.RefundRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface RefundRecordMapper {
    @Insert("INSERT INTO refund_records (refund_no, order_id, order_no, order_item_id, user_id, refund_type, " +
            "refund_reason_type, refund_reason, refund_amount, refund_status, evidence_images, remark, " +
            "created_at, updated_at) VALUES (#{refundNo}, #{orderId}, #{orderNo}, #{orderItemId}, #{userId}, " +
            "#{refundType}, #{refundReasonType}, #{refundReason}, #{refundAmount}, #{refundStatus}, " +
            "#{evidenceImages}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RefundRecord record);

    @Select("SELECT * FROM refund_records WHERE id = #{id}")
    RefundRecord selectById(Long id);

    @Select("SELECT * FROM refund_records WHERE refund_no = #{refundNo}")
    RefundRecord selectByRefundNo(String refundNo);

    @Select("SELECT * FROM refund_records WHERE order_id = #{orderId}")
    List<RefundRecord> selectByOrderId(Long orderId);

    @Select("SELECT * FROM refund_records WHERE order_no = #{orderNo}")
    List<RefundRecord> selectByOrderNo(String orderNo);

    @Select("SELECT * FROM refund_records WHERE order_item_id = #{orderItemId}")
    RefundRecord selectByOrderItemId(Long orderItemId);

    @Select("<script>" +
            "SELECT * FROM refund_records WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='refundStatus != null'> AND refund_status = #{refundStatus}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<RefundRecord> selectList(@Param("userId") Long userId, @Param("refundStatus") Integer refundStatus,
                                 @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                 @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM refund_records WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='refundStatus != null'> AND refund_status = #{refundStatus}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            "</script>")
    long selectCount(@Param("userId") Long userId, @Param("refundStatus") Integer refundStatus,
                    @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Update("UPDATE refund_records SET refund_status = #{refundStatus}, refund_time = #{refundTime}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateRefundStatus(@Param("id") Long id, @Param("refundStatus") Integer refundStatus,
                          @Param("refundTime") LocalDateTime refundTime);

    @Update("UPDATE refund_records SET refund_status = #{refundStatus}, reject_reason = #{rejectReason}, " +
            "reject_time = #{rejectTime}, updated_at = NOW() WHERE id = #{id}")
    int updateRejectInfo(@Param("id") Long id, @Param("refundStatus") Integer refundStatus,
                        @Param("rejectReason") String rejectReason, @Param("rejectTime") LocalDateTime rejectTime);

    @Select("SELECT COUNT(*) FROM refund_records WHERE order_id = #{orderId}")
    int countByOrderId(Long orderId);
} 