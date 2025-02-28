package com.campus.api.mapper;

import com.campus.api.entity.OrderLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderLogMapper {
    @Insert("INSERT INTO order_logs (order_id, order_no, operator_id, operator_type, action_type, " +
            "action_desc, ip, created_at) VALUES (#{orderId}, #{orderNo}, #{operatorId}, #{operatorType}, " +
            "#{actionType}, #{actionDesc}, #{ip}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderLog log);

    @Select("SELECT * FROM order_logs WHERE order_id = #{orderId} ORDER BY created_at DESC")
    List<OrderLog> selectByOrderId(Long orderId);

    @Select("SELECT * FROM order_logs WHERE order_no = #{orderNo} ORDER BY created_at DESC")
    List<OrderLog> selectByOrderNo(String orderNo);

    @Select("<script>" +
            "SELECT * FROM order_logs WHERE 1=1" +
            "<if test='operatorId != null'> AND operator_id = #{operatorId}</if>" +
            "<if test='operatorType != null'> AND operator_type = #{operatorType}</if>" +
            "<if test='actionType != null'> AND action_type = #{actionType}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<OrderLog> selectList(@Param("operatorId") Long operatorId, @Param("operatorType") Integer operatorType,
                             @Param("actionType") Integer actionType, @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM order_logs WHERE 1=1" +
            "<if test='operatorId != null'> AND operator_id = #{operatorId}</if>" +
            "<if test='operatorType != null'> AND operator_type = #{operatorType}</if>" +
            "<if test='actionType != null'> AND action_type = #{actionType}</if>" +
            "</script>")
    long selectCount(@Param("operatorId") Long operatorId, @Param("operatorType") Integer operatorType,
                    @Param("actionType") Integer actionType);
} 