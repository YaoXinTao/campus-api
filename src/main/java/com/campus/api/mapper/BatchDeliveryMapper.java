package com.campus.api.mapper;

import com.campus.api.dto.BatchDeliveryDTO;
import com.campus.api.dto.BatchDeliveryDetailDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BatchDeliveryMapper {
    
    @Insert("INSERT INTO batch_delivery_records (batch_no, operator_id, total_count, status, created_at, updated_at) " +
            "VALUES (#{batchNo}, #{operatorId}, #{totalCount}, 0, NOW(), NOW())")
    void insertBatchDelivery(@Param("batchNo") String batchNo, @Param("operatorId") Long operatorId,
                            @Param("totalCount") Integer totalCount);

    @Insert("INSERT INTO batch_delivery_items (batch_no, order_id, order_no, delivery_company, delivery_no, " +
            "status, created_at, updated_at) " +
            "VALUES (#{batchNo}, #{delivery.orderId}, #{delivery.orderNo}, #{delivery.deliveryCompany}, " +
            "#{delivery.deliveryNo}, 0, NOW(), NOW())")
    void insertBatchDeliveryItem(@Param("batchNo") String batchNo, @Param("delivery") BatchDeliveryDTO delivery);

    @Select("SELECT * FROM batch_delivery_items WHERE batch_no = #{batchNo} AND status = 0")
    List<BatchDeliveryDTO> selectPendingDeliveryItems(String batchNo);

    @Update("UPDATE batch_delivery_items SET status = #{status}, error_msg = #{errorMsg}, " +
            "updated_at = NOW() WHERE batch_no = #{batchNo} AND order_id = #{orderId}")
    void updateDeliveryItemStatus(@Param("batchNo") String batchNo, @Param("orderId") Long orderId,
                                 @Param("status") Integer status, @Param("errorMsg") String errorMsg);

    @Update("UPDATE batch_delivery_records SET status = #{status}, success_count = #{successCount}, " +
            "fail_count = #{failCount}, updated_at = NOW() WHERE batch_no = #{batchNo}")
    void updateBatchDeliveryStatus(@Param("batchNo") String batchNo, @Param("status") Integer status,
                                  @Param("successCount") Integer successCount, @Param("failCount") Integer failCount);

    @Select("SELECT r.*, u.username as operator_name, " +
            "(SELECT COUNT(*) FROM batch_delivery_items i WHERE i.batch_no = r.batch_no AND i.status = 1) as success_count, " +
            "(SELECT COUNT(*) FROM batch_delivery_items i WHERE i.batch_no = r.batch_no AND i.status = 2) as fail_count " +
            "FROM batch_delivery_records r " +
            "LEFT JOIN admin_users u ON r.operator_id = u.id " +
            "WHERE r.batch_no = #{batchNo}")
    @Results({
            @Result(property = "items", column = "batch_no",
                    many = @Many(select = "selectBatchDeliveryItems"))
    })
    BatchDeliveryDetailDTO selectBatchDeliveryDetail(String batchNo);

    @Select("SELECT * FROM batch_delivery_items WHERE batch_no = #{batchNo}")
    List<BatchDeliveryDetailDTO.BatchDeliveryItemDTO> selectBatchDeliveryItems(String batchNo);

    @Select("SELECT r.*, u.username as operator_name, " +
            "(SELECT COUNT(*) FROM batch_delivery_items i WHERE i.batch_no = r.batch_no AND i.status = 1) as success_count, " +
            "(SELECT COUNT(*) FROM batch_delivery_items i WHERE i.batch_no = r.batch_no AND i.status = 2) as fail_count " +
            "FROM batch_delivery_records r " +
            "LEFT JOIN admin_users u ON r.operator_id = u.id " +
            "ORDER BY r.created_at DESC")
    List<BatchDeliveryDetailDTO> selectBatchDeliveryList();

    @Update("UPDATE batch_delivery_items SET status = 0, error_msg = NULL, updated_at = NOW() " +
            "WHERE batch_no = #{batchNo} AND status = 2")
    void resetFailedDeliveryItems(String batchNo);
} 