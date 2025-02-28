package com.campus.api.service;

import com.campus.api.dto.BatchDeliveryDTO;
import com.campus.api.dto.BatchDeliveryDetailDTO;
import com.campus.api.common.PageResult;
import java.util.List;

public interface BatchDeliveryService {
    /**
     * 创建批量发货记录
     *
     * @param operatorId 操作人ID
     * @param deliveryList 发货信息列表
     * @return 批次号
     */
    String createBatchDelivery(Long operatorId, List<BatchDeliveryDTO> deliveryList);

    /**
     * 处理批量发货
     *
     * @param batchNo 批次号
     */
    void processBatchDelivery(String batchNo);

    /**
     * 获取批量发货记录详情
     *
     * @param batchNo 批次号
     * @return 批量发货详情
     */
    BatchDeliveryDetailDTO getBatchDeliveryDetail(String batchNo);

    /**
     * 获取批量发货记录列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 批量发货记录列表
     */
    PageResult<BatchDeliveryDetailDTO> getBatchDeliveryList(int pageNum, int pageSize);

    /**
     * 重试失败的发货记录
     *
     * @param batchNo 批次号
     */
    void retryFailedDelivery(String batchNo);
} 