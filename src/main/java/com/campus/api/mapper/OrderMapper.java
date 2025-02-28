package com.campus.api.mapper;

import com.campus.api.entity.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    @Insert("INSERT INTO orders (order_no, user_id, total_amount, actual_amount, freight_amount, discount_amount, " +
            "coupon_id, address_id, receiver, phone, province, city, district, detail_address, order_status, " +
            "payment_status, delivery_status, remark, created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{actualAmount}, #{freightAmount}, #{discountAmount}, " +
            "#{couponId}, #{addressId}, #{receiver}, #{phone}, #{province}, #{city}, #{district}, #{detailAddress}, " +
            "#{orderStatus}, #{paymentStatus}, #{deliveryStatus}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order selectById(Long id);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    @Select("<script>" +
            "SELECT * FROM orders WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='orderNo != null and orderNo != \"\"'> AND order_no = #{orderNo}</if>" +
            "<if test='orderStatus != null'> AND order_status = #{orderStatus}</if>" +
            "<if test='paymentStatus != null'> AND payment_status = #{paymentStatus}</if>" +
            "<if test='deliveryStatus != null'> AND delivery_status = #{deliveryStatus}</if>" +
            "<if test='receiver != null and receiver != \"\"'> AND receiver LIKE CONCAT('%', #{receiver}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Order> selectList(@Param("userId") Long userId, @Param("orderNo") String orderNo,
                          @Param("orderStatus") Integer orderStatus, @Param("paymentStatus") Integer paymentStatus,
                          @Param("deliveryStatus") Integer deliveryStatus, @Param("receiver") String receiver,
                          @Param("phone") String phone, @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime, @Param("offset") Integer offset,
                          @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM orders WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='orderNo != null and orderNo != \"\"'> AND order_no = #{orderNo}</if>" +
            "<if test='orderStatus != null'> AND order_status = #{orderStatus}</if>" +
            "<if test='paymentStatus != null'> AND payment_status = #{paymentStatus}</if>" +
            "<if test='deliveryStatus != null'> AND delivery_status = #{deliveryStatus}</if>" +
            "<if test='receiver != null and receiver != \"\"'> AND receiver LIKE CONCAT('%', #{receiver}, '%')</if>" +
            "<if test='phone != null and phone != \"\"'> AND phone = #{phone}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            "</script>")
    long selectCount(@Param("userId") Long userId, @Param("orderNo") String orderNo,
                    @Param("orderStatus") Integer orderStatus, @Param("paymentStatus") Integer paymentStatus,
                    @Param("deliveryStatus") Integer deliveryStatus, @Param("receiver") String receiver,
                    @Param("phone") String phone, @Param("startTime") LocalDateTime startTime,
                    @Param("endTime") LocalDateTime endTime);

    @Update("UPDATE orders SET order_status = #{orderStatus}, updated_at = NOW() WHERE id = #{id}")
    int updateOrderStatus(@Param("id") Long id, @Param("orderStatus") Integer orderStatus);

    @Update("UPDATE orders SET payment_status = #{status}, payment_time = #{paymentTime}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updatePaymentStatus(@Param("id") Long id, @Param("status") Integer status, 
            @Param("paymentTime") LocalDateTime paymentTime);

    @Update("UPDATE orders SET payment_method = #{paymentMethod}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updatePaymentMethod(@Param("id") Long id, @Param("paymentMethod") Integer paymentMethod);

    @Update("UPDATE orders SET delivery_status = #{status}, delivery_time = #{deliveryTime}, " +
            "updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    int updateDeliveryStatus(@Param("id") Long id, @Param("status") Integer status,
            @Param("deliveryTime") LocalDateTime deliveryTime);

    @Update("UPDATE orders SET receive_time = #{receiveTime}, updated_at = NOW() WHERE id = #{id}")
    int updateReceiveTime(@Param("id") Long id, @Param("receiveTime") LocalDateTime receiveTime);

    @Update("UPDATE orders SET finish_time = #{finishTime}, updated_at = NOW() WHERE id = #{id}")
    int updateFinishTime(@Param("id") Long id, @Param("finishTime") LocalDateTime finishTime);

    @Update("UPDATE orders SET cancel_time = #{cancelTime}, cancel_reason = #{cancelReason}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateCancelInfo(@Param("id") Long id, @Param("cancelTime") LocalDateTime cancelTime,
                        @Param("cancelReason") String cancelReason);

    @Select("SELECT COUNT(*) > 0 FROM orders WHERE id = #{orderId} AND user_id = #{userId}")
    boolean checkOrderBelongsToUser(@Param("orderId") Long orderId, @Param("userId") Long userId);
    
    @Update("UPDATE order_items SET review_status = 1 WHERE id = #{orderItemId}")
    int updateOrderItemReviewStatus(@Param("orderItemId") Long orderItemId);
    
    @Select("SELECT COUNT(*) = 0 FROM order_items WHERE order_id = #{orderId} AND review_status = 0")
    boolean checkAllItemsReviewed(@Param("orderId") Long orderId);
    
    @Update("UPDATE orders SET review_status = 1 WHERE id = #{orderId}")
    int updateOrderReviewStatus(@Param("orderId") Long orderId);
    
    @Select("SELECT COUNT(*) = 0 FROM order_items WHERE id = #{orderItemId} AND refund_status != 0")
    boolean checkOrderItemCanRefund(@Param("orderItemId") Long orderItemId);
    
    @Select("SELECT quantity FROM order_items WHERE id = #{orderItemId}")
    int getOrderItemQuantity(@Param("orderItemId") Long orderItemId);
    
    @Update("UPDATE order_items SET refund_status = #{status} WHERE id = #{orderItemId}")
    int updateOrderItemRefundStatus(@Param("orderItemId") Long orderItemId, @Param("status") int status);
    
    @Update("UPDATE order_items SET refunded_quantity = #{quantity} WHERE id = #{orderItemId}")
    int updateOrderItemRefundedQuantity(@Param("orderItemId") Long orderItemId, @Param("quantity") Integer quantity);
    
    @Select("SELECT COUNT(*) = (SELECT COUNT(*) FROM order_items WHERE order_id = #{orderId}) " +
            "FROM order_items WHERE order_id = #{orderId} AND refund_status = 2")
    boolean checkAllItemsRefunded(@Param("orderId") Long orderId);
    
    @Select("SELECT COUNT(*) = 0 FROM orders WHERE id = #{orderId} AND delivery_status != 0")
    boolean checkOrderCanDeliver(@Param("orderId") Long orderId);
    
    @Insert("INSERT INTO order_delivery (order_id, delivery_company, delivery_no, tracking_data) " +
            "VALUES (#{orderId}, #{company}, #{no}, #{data})")
    int insertOrderDelivery(@Param("orderId") Long orderId, @Param("company") String company,
                          @Param("no") String no, @Param("data") String data);
    
    @Update("UPDATE orders SET delivery_status = 1 WHERE id = #{orderId}")
    int updateOrderDeliveryStatus(@Param("orderId") Long orderId);

    @Select("SELECT CASE " +
            "WHEN oi.quantity >= #{refundQuantity} AND oi.total_amount >= #{refundAmount} " +
            "THEN true ELSE false END " +
            "FROM order_items oi WHERE oi.id = #{orderItemId}")
    boolean checkRefundAmountValid(@Param("orderItemId") Long orderItemId,
                                 @Param("refundAmount") BigDecimal refundAmount,
                                 @Param("refundQuantity") Integer refundQuantity);
} 