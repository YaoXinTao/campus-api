package com.campus.api.mapper;

import com.campus.api.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    @Insert("INSERT INTO order_items (order_id, order_no, product_id, sku_id, product_name, product_image, " +
            "sku_spec_data, price, quantity, total_amount, refund_status, created_at, updated_at) " +
            "VALUES (#{orderId}, #{orderNo}, #{productId}, #{skuId}, #{productName}, #{productImage}, " +
            "#{skuSpecData}, #{price}, #{quantity}, #{totalAmount}, #{refundStatus}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem orderItem);

    @Insert("<script>" +
            "INSERT INTO order_items (order_id, order_no, product_id, sku_id, product_name, product_image, " +
            "sku_spec_data, price, quantity, total_amount, refund_status, created_at, updated_at) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.orderId}, #{item.orderNo}, #{item.productId}, #{item.skuId}, #{item.productName}, " +
            "#{item.productImage}, #{item.skuSpecData}, #{item.price}, #{item.quantity}, #{item.totalAmount}, " +
            "#{item.refundStatus}, #{item.createdAt}, #{item.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("items") List<OrderItem> items);

    @Select("SELECT * FROM order_items WHERE id = #{id}")
    OrderItem selectById(Long id);

    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(Long orderId);

    @Select("SELECT * FROM order_items WHERE order_no = #{orderNo}")
    List<OrderItem> selectByOrderNo(String orderNo);

    @Update("UPDATE order_items SET refund_status = #{refundStatus}, refund_id = #{refundId}, " +
            "updated_at = NOW() WHERE id = #{id}")
    int updateRefundStatus(@Param("id") Long id, @Param("refundStatus") Integer refundStatus,
                          @Param("refundId") Long refundId);

    @Select("SELECT COUNT(*) FROM order_items WHERE order_id = #{orderId} AND refund_status != 0")
    int countRefundItems(Long orderId);
} 