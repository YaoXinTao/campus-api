package com.campus.api.mapper;

import com.campus.api.entity.ExchangeDelivery;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExchangeDeliveryMapper {
    @Insert("INSERT INTO exchange_delivery (exchange_id, exchange_no, delivery_type, delivery_company, " +
            "delivery_no, sender_name, sender_phone, sender_address, receiver_name, receiver_phone, " +
            "receiver_address, delivery_status, delivery_time, receive_time, tracking_data, created_at, " +
            "updated_at) VALUES (#{exchangeId}, #{exchangeNo}, #{deliveryType}, #{deliveryCompany}, " +
            "#{deliveryNo}, #{senderName}, #{senderPhone}, #{senderAddress}, #{receiverName}, " +
            "#{receiverPhone}, #{receiverAddress}, #{deliveryStatus}, #{deliveryTime}, #{receiveTime}, " +
            "#{trackingData}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExchangeDelivery delivery);

    @Select("SELECT * FROM exchange_delivery WHERE id = #{id}")
    ExchangeDelivery selectById(Long id);

    @Select("SELECT * FROM exchange_delivery WHERE exchange_id = #{exchangeId}")
    List<ExchangeDelivery> selectByExchangeId(Long exchangeId);

    @Select("SELECT * FROM exchange_delivery WHERE exchange_no = #{exchangeNo}")
    List<ExchangeDelivery> selectByExchangeNo(String exchangeNo);

    @Select("SELECT * FROM exchange_delivery WHERE exchange_id = #{exchangeId} AND delivery_type = #{deliveryType}")
    ExchangeDelivery selectByExchangeIdAndType(@Param("exchangeId") Long exchangeId,
                                              @Param("deliveryType") Integer deliveryType);

    @Update("UPDATE exchange_delivery SET delivery_status = #{deliveryStatus}, delivery_time = #{deliveryTime}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateDeliveryStatus(@Param("id") Long id, @Param("deliveryStatus") Integer deliveryStatus,
                            @Param("deliveryTime") LocalDateTime deliveryTime);

    @Update("UPDATE exchange_delivery SET receive_time = #{receiveTime}, delivery_status = #{deliveryStatus}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateReceiveInfo(@Param("id") Long id, @Param("receiveTime") LocalDateTime receiveTime,
                         @Param("deliveryStatus") Integer deliveryStatus);

    @Update("UPDATE exchange_delivery SET tracking_data = #{trackingData}, updated_at = NOW() WHERE id = #{id}")
    int updateTrackingData(@Param("id") Long id, @Param("trackingData") String trackingData);
} 