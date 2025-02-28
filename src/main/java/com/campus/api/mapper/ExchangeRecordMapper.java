package com.campus.api.mapper;

import com.campus.api.entity.ExchangeRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExchangeRecordMapper {
    @Insert("INSERT INTO exchange_records (exchange_no, order_id, order_no, order_item_id, user_id, " +
            "old_sku_id, new_sku_id, old_sku_spec_data, new_sku_spec_data, exchange_reason_type, " +
            "exchange_reason, exchange_status, evidence_images, remark, created_at, updated_at) " +
            "VALUES (#{exchangeNo}, #{orderId}, #{orderNo}, #{orderItemId}, #{userId}, #{oldSkuId}, " +
            "#{newSkuId}, #{oldSkuSpecData}, #{newSkuSpecData}, #{exchangeReasonType}, #{exchangeReason}, " +
            "#{exchangeStatus}, #{evidenceImages}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ExchangeRecord record);

    @Select("SELECT * FROM exchange_records WHERE id = #{id}")
    ExchangeRecord selectById(Long id);

    @Select("SELECT * FROM exchange_records WHERE exchange_no = #{exchangeNo}")
    ExchangeRecord selectByExchangeNo(String exchangeNo);

    @Select("SELECT * FROM exchange_records WHERE order_id = #{orderId}")
    List<ExchangeRecord> selectByOrderId(Long orderId);

    @Select("SELECT * FROM exchange_records WHERE order_no = #{orderNo}")
    List<ExchangeRecord> selectByOrderNo(String orderNo);

    @Select("SELECT * FROM exchange_records WHERE order_item_id = #{orderItemId}")
    ExchangeRecord selectByOrderItemId(Long orderItemId);

    @Select("<script>" +
            "SELECT * FROM exchange_records WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='exchangeStatus != null'> AND exchange_status = #{exchangeStatus}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<ExchangeRecord> selectList(@Param("userId") Long userId, @Param("exchangeStatus") Integer exchangeStatus,
                                   @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                   @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM exchange_records WHERE 1=1" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='exchangeStatus != null'> AND exchange_status = #{exchangeStatus}</if>" +
            "<if test='startTime != null'> AND created_at >= #{startTime}</if>" +
            "<if test='endTime != null'> AND created_at &lt;= #{endTime}</if>" +
            "</script>")
    long selectCount(@Param("userId") Long userId, @Param("exchangeStatus") Integer exchangeStatus,
                    @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Update("UPDATE exchange_records SET exchange_status = #{exchangeStatus}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int updateExchangeStatus(@Param("id") Long id, @Param("exchangeStatus") Integer exchangeStatus);

    @Update("UPDATE exchange_records SET exchange_status = #{exchangeStatus}, reject_reason = #{rejectReason}, " +
            "reject_time = #{rejectTime}, updated_at = NOW() WHERE id = #{id}")
    int updateRejectInfo(@Param("id") Long id, @Param("exchangeStatus") Integer exchangeStatus,
                        @Param("rejectReason") String rejectReason, @Param("rejectTime") LocalDateTime rejectTime);

    @Update("UPDATE exchange_records SET finish_time = #{finishTime}, updated_at = NOW() WHERE id = #{id}")
    int updateFinishTime(@Param("id") Long id, @Param("finishTime") LocalDateTime finishTime);

    @Update("UPDATE exchange_records SET cancel_time = #{cancelTime}, updated_at = NOW() WHERE id = #{id}")
    int updateCancelTime(@Param("id") Long id, @Param("cancelTime") LocalDateTime cancelTime);

    @Select("<script>" +
            "SELECT * FROM exchange_records WHERE exchange_status = #{exchangeStatus} AND " +
            "finish_time IS NULL AND cancel_time IS NULL AND created_at &lt;= #{deadline}" +
            "</script>")
    List<ExchangeRecord> selectTimeoutApplyList(@Param("exchangeStatus") Integer exchangeStatus, @Param("deadline") LocalDateTime deadline);

    @Update("<script>" +
            "UPDATE exchange_records SET exchange_status = #{exchangeStatus}, updated_at = #{updatedAt} WHERE id IN (" +
            "<foreach collection='ids' item='id' separator=','>" +
            "#{id}" +
            "</foreach>" +
            ")</script>")
    int batchUpdateExchangeStatus(@Param("ids") List<Long> ids, @Param("exchangeStatus") Integer exchangeStatus, @Param("updatedAt") LocalDateTime updatedAt);
} 