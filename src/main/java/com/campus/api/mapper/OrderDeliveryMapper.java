package com.campus.api.mapper;

import com.campus.api.entity.OrderDelivery;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderDeliveryMapper {
    @Insert("INSERT INTO order_delivery (order_id, order_no, delivery_company, delivery_no, delivery_status, " +
            "delivery_time, receive_time, tracking_data, created_at, updated_at) " +
            "VALUES (#{orderId}, #{orderNo}, #{deliveryCompany}, #{deliveryNo}, #{deliveryStatus}, " +
            "#{deliveryTime}, #{receiveTime}, #{trackingData}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderDelivery delivery);

    @Select("SELECT * FROM order_delivery WHERE id = #{id}")
    OrderDelivery selectById(Long id);

    @Select("SELECT * FROM order_delivery WHERE order_id = #{orderId}")
    OrderDelivery selectByOrderId(Long orderId);

    @Select("SELECT * FROM order_delivery WHERE order_no = #{orderNo}")
    OrderDelivery selectByOrderNo(String orderNo);

    @Update("UPDATE order_delivery SET delivery_status = #{deliveryStatus}, delivery_time = #{deliveryTime}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateDeliveryStatus(@Param("id") Long id, @Param("deliveryStatus") Integer deliveryStatus,
                            @Param("deliveryTime") LocalDateTime deliveryTime);

    @Update("UPDATE order_delivery SET receive_time = #{receiveTime}, delivery_status = #{deliveryStatus}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateReceiveInfo(@Param("id") Long id, @Param("receiveTime") LocalDateTime receiveTime,
                         @Param("deliveryStatus") Integer deliveryStatus);

    @Update("UPDATE order_delivery SET tracking_data = #{trackingData}, updated_at = NOW() WHERE id = #{id}")
    int updateTrackingData(@Param("id") Long id, @Param("trackingData") String trackingData);
} 