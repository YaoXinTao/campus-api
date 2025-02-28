package com.campus.api.mapper;

import com.campus.api.entity.PaymentRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PaymentRecordMapper {
    @Insert("INSERT INTO payment_records (payment_no, order_id, order_no, user_id, payment_method, payment_amount, " +
            "transaction_id, payment_status, payment_time, callback_time, callback_content, created_at, updated_at) " +
            "VALUES (#{paymentNo}, #{orderId}, #{orderNo}, #{userId}, #{paymentMethod}, #{paymentAmount}, " +
            "#{transactionId}, #{paymentStatus}, #{paymentTime}, #{callbackTime}, #{callbackContent}, " +
            "#{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PaymentRecord record);

    @Select("SELECT * FROM payment_records WHERE id = #{id}")
    PaymentRecord selectById(Long id);

    @Select("SELECT * FROM payment_records WHERE payment_no = #{paymentNo}")
    PaymentRecord selectByPaymentNo(String paymentNo);

    @Select("SELECT * FROM payment_records WHERE order_id = #{orderId}")
    List<PaymentRecord> selectByOrderId(Long orderId);

    @Select("SELECT * FROM payment_records WHERE order_no = #{orderNo}")
    List<PaymentRecord> selectByOrderNo(String orderNo);

    @Update("UPDATE payment_records SET payment_status = #{paymentStatus}, payment_time = #{paymentTime}, " +
            "transaction_id = #{transactionId}, updated_at = NOW() WHERE id = #{id}")
    int updatePaymentStatus(@Param("id") Long id, @Param("paymentStatus") Integer paymentStatus,
                          @Param("paymentTime") java.time.LocalDateTime paymentTime,
                          @Param("transactionId") String transactionId);

    @Update("UPDATE payment_records SET callback_time = #{callbackTime}, callback_content = #{callbackContent}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateCallback(@Param("id") Long id, @Param("callbackTime") java.time.LocalDateTime callbackTime,
                      @Param("callbackContent") String callbackContent);
} 