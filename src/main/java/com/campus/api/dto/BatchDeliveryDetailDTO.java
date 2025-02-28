package com.campus.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BatchDeliveryDetailDTO {
    /**
     * 批量发货ID
     */
    private Long id;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 总订单数
     */
    private Integer totalCount;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 状态：0-处理中 1-处理完成 2-处理失败
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 发货详情列表
     */
    private List<BatchDeliveryItemDTO> items;

    @Data
    public static class BatchDeliveryItemDTO {
        /**
         * 详情ID
         */
        private Long id;

        /**
         * 订单ID
         */
        private Long orderId;

        /**
         * 订单编号
         */
        private String orderNo;

        /**
         * 物流公司
         */
        private String deliveryCompany;

        /**
         * 物流单号
         */
        private String deliveryNo;

        /**
         * 状态：0-待处理 1-成功 2-失败
         */
        private Integer status;

        /**
         * 错误信息
         */
        private String errorMsg;

        /**
         * 创建时间
         */
        private LocalDateTime createdAt;
    }
} 