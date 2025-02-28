package com.campus.api.service.impl;

import com.campus.api.service.BatchDeliveryService;
import com.campus.api.service.DistributedLockService;
import com.campus.api.mapper.BatchDeliveryMapper;
import com.campus.api.mapper.OrderMapper;
import com.campus.api.dto.BatchDeliveryDTO;
import com.campus.api.dto.BatchDeliveryDetailDTO;
import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.common.util.SnowflakeIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BatchDeliveryServiceImpl implements BatchDeliveryService {

    @Autowired
    private BatchDeliveryMapper batchDeliveryMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DistributedLockService distributedLockService;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createBatchDelivery(Long operatorId, List<BatchDeliveryDTO> deliveryList) {
        if (deliveryList == null || deliveryList.isEmpty()) {
            throw new BusinessException("发货列表不能为空");
        }

        // 生成批次号
        String batchNo = "B" + idGenerator.nextId();

        // 创建批量发货记录
        batchDeliveryMapper.insertBatchDelivery(batchNo, operatorId, deliveryList.size());

        // 创建批量发货详情
        for (BatchDeliveryDTO delivery : deliveryList) {
            batchDeliveryMapper.insertBatchDeliveryItem(batchNo, delivery);
        }

        return batchNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processBatchDelivery(String batchNo) {
        // 获取分布式锁
        String lockKey = "batch_delivery:" + batchNo;
        String lockValue = distributedLockService.tryLock(lockKey, 300); // 5分钟超时
        if (!"OK".equals(lockValue)) {
            throw new BusinessException("该批次正在处理中");
        }

        try {
            // 获取待处理的发货记录
            List<BatchDeliveryDTO> pendingList = batchDeliveryMapper.selectPendingDeliveryItems(batchNo);
            if (pendingList.isEmpty()) {
                return;
            }

            int successCount = 0;
            int failCount = 0;

            // 处理每个发货记录
            for (BatchDeliveryDTO delivery : pendingList) {
                try {
                    // 检查订单状态
                    if (!orderMapper.checkOrderCanDeliver(delivery.getOrderId())) {
                        throw new BusinessException("订单状态不允许发货");
                    }

                    // 创建订单物流记录
                    orderMapper.insertOrderDelivery(delivery.getOrderId(), delivery.getOrderNo(),
                            delivery.getDeliveryCompany(), delivery.getDeliveryNo());

                    // 更新订单发货状态
                    orderMapper.updateOrderDeliveryStatus(delivery.getOrderId());

                    // 更新发货记录状态为成功
                    batchDeliveryMapper.updateDeliveryItemStatus(batchNo, delivery.getOrderId(), 1, null);
                    successCount++;
                } catch (Exception e) {
                    // 更新发货记录状态为失败
                    batchDeliveryMapper.updateDeliveryItemStatus(batchNo, delivery.getOrderId(), 2, e.getMessage());
                    failCount++;
                }
            }

            // 更新批量发货记录状态
            int status = failCount == 0 ? 1 : (successCount == 0 ? 2 : 1);
            batchDeliveryMapper.updateBatchDeliveryStatus(batchNo, status, successCount, failCount);

        } finally {
            // 释放分布式锁
            distributedLockService.releaseLock(lockKey, lockValue);
        }
    }

    @Override
    public BatchDeliveryDetailDTO getBatchDeliveryDetail(String batchNo) {
        return batchDeliveryMapper.selectBatchDeliveryDetail(batchNo);
    }

    @Override
    public PageResult<BatchDeliveryDetailDTO> getBatchDeliveryList(int pageNum, int pageSize) {
        Page<BatchDeliveryDetailDTO> page = PageHelper.startPage(pageNum, pageSize)
                .doSelectPage(() -> batchDeliveryMapper.selectBatchDeliveryList());
        
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void retryFailedDelivery(String batchNo) {
        // 重置失败记录的状态为待处理
        batchDeliveryMapper.resetFailedDeliveryItems(batchNo);
        // 重置批量发货记录的状态
        batchDeliveryMapper.updateBatchDeliveryStatus(batchNo, 0, null, null);
        // 重新处理
        processBatchDelivery(batchNo);
    }
} 