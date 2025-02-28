package com.campus.api.task;

import com.campus.api.entity.ExchangeRecord;
import com.campus.api.entity.OrderLog;
import com.campus.api.mapper.ExchangeRecordMapper;
import com.campus.api.mapper.OrderItemMapper;
import com.campus.api.mapper.OrderLogMapper;
import com.campus.api.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeTimeoutTask {

    private final ExchangeRecordMapper exchangeRecordMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderLogMapper orderLogMapper;
    private final MessageService messageService;

    /**
     * 处理换货申请超时未处理的订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void handleApplyTimeout() {
        log.info("开始处理换货申请超时订单...");
        // 查询超过24小时未处理的换货申请
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<ExchangeRecord> records = exchangeRecordMapper.selectTimeoutApplyList(0, deadline);

        for (ExchangeRecord record : records) {
            try {
                // 更新换货状态为已取消
                record.setExchangeStatus(8);
                record.setUpdatedAt(LocalDateTime.now());
                exchangeRecordMapper.updateExchangeStatus(record.getId(), 8);

                // 更新订单商品状态
                orderItemMapper.updateRefundStatus(record.getOrderItemId(), 0, null);

                // 记录操作日志
                saveOrderLog(record.getOrderId(), record.getOrderNo(), 0L, 3, 19, 
                        "换货申请超时未处理，系统自动取消");

                log.info("换货申请超时订单处理成功，exchangeId={}", record.getId());
            } catch (Exception e) {
                log.error("换货申请超时订单处理失败，exchangeId=" + record.getId(), e);
            }
        }
        log.info("处理换货申请超时订单完成，处理数量：{}", records.size());
    }

    /**
     * 处理买家超时未退货的订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void handleReturnTimeout() {
        log.info("开始处理买家超时未退货订单...");
        // 查询超过7天未退货的订单
        LocalDateTime deadline = LocalDateTime.now().minusDays(7);
        List<ExchangeRecord> records = exchangeRecordMapper.selectTimeoutApplyList(3, deadline);

        for (ExchangeRecord record : records) {
            try {
                // 更新换货状态为已取消
                record.setExchangeStatus(8);
                record.setUpdatedAt(LocalDateTime.now());
                exchangeRecordMapper.updateExchangeStatus(record.getId(), 8);

                // 更新订单商品状态
                orderItemMapper.updateRefundStatus(record.getOrderItemId(), 0, null);

                // 记录操作日志
                saveOrderLog(record.getOrderId(), record.getOrderNo(), 0L, 3, 20, 
                        "买家超时未退货，系统自动取消换货");

                log.info("买家超时未退货订单处理成功，exchangeId={}", record.getId());
            } catch (Exception e) {
                log.error("买家超时未退货订单处理失败，exchangeId=" + record.getId(), e);
            }
        }
        log.info("处理买家超时未退货订单完成，处理数量：{}", records.size());
    }

    /**
     * 处理商家超时未确认收货的订单
     * 每5分钟执行一次
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void handleConfirmTimeout() {
        log.info("开始处理商家超时未确认收货订单...");
        // 查询超过48小时未确认收货的订单
        LocalDateTime deadline = LocalDateTime.now().minusHours(48);
        List<ExchangeRecord> records = exchangeRecordMapper.selectTimeoutApplyList(4, deadline);

        for (ExchangeRecord record : records) {
            try {
                // 更新换货状态为待发货
                record.setExchangeStatus(5);
                record.setUpdatedAt(LocalDateTime.now());
                exchangeRecordMapper.updateExchangeStatus(record.getId(), 5);

                // 记录操作日志
                saveOrderLog(record.getOrderId(), record.getOrderNo(), 0L, 3, 21, 
                        "商家超时未确认收货，系统自动确认");

                log.info("商家超时未确认收货订单处理成功，exchangeId={}", record.getId());
            } catch (Exception e) {
                log.error("商家超时未确认收货订单处理失败，exchangeId=" + record.getId(), e);
            }
        }
        log.info("处理商家超时未确认收货订单完成，处理数量：{}", records.size());
    }

    /**
     * 保存订单操作日志
     */
    private void saveOrderLog(Long orderId, String orderNo, Long operatorId, Integer operatorType,
                            Integer actionType, String actionDesc) {
        OrderLog log = new OrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setOperatorId(operatorId);
        log.setOperatorType(operatorType);
        log.setActionType(actionType);
        log.setActionDesc(actionDesc);
        log.setCreatedAt(LocalDateTime.now());
        orderLogMapper.insert(log);
    }
} 