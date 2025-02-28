package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.BatchDeliveryDTO;
import com.campus.api.dto.BatchDeliveryDetailDTO;
import com.campus.api.service.BatchDeliveryService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员批量发货接口", description = "后台批量发货管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/batch-delivery")
@RequiredArgsConstructor
public class AdminBatchDeliveryController {

    private final BatchDeliveryService batchDeliveryService;

    /**
     * 创建批量发货记录
     */
    @Operation(summary = "创建批量发货任务")
    @PostMapping("/create")
    public Result<String> createBatchDelivery(@RequestBody @Valid List<BatchDeliveryDTO> deliveryList) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        return Result.success(batchDeliveryService.createBatchDelivery(operatorId, deliveryList));
    }

    /**
     * 处理批量发货
     */
    @Operation(summary = "处理批量发货")
    @PostMapping("/{batchNo}/process")
    public Result<Void> processBatchDelivery(@PathVariable String batchNo) {
        batchDeliveryService.processBatchDelivery(batchNo);
        return Result.success();
    }

    /**
     * 获取批量发货记录详情
     */
    @Operation(summary = "获取批量发货详情")
    @GetMapping("/{batchNo}")
    public Result<BatchDeliveryDetailDTO> getBatchDeliveryDetail(@PathVariable String batchNo) {
        return Result.success(batchDeliveryService.getBatchDeliveryDetail(batchNo));
    }

    /**
     * 获取批量发货记录列表
     */
    @Operation(summary = "获取批量发货列表")
    @GetMapping("/list")
    public Result<PageResult<BatchDeliveryDetailDTO>> getBatchDeliveryList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(batchDeliveryService.getBatchDeliveryList(pageNum, pageSize));
    }

    /**
     * 重试失败的发货记录
     */
    @Operation(summary = "重试失败的发货记录")
    @PostMapping("/{batchNo}/retry")
    public Result<Void> retryFailedDelivery(@PathVariable String batchNo) {
        batchDeliveryService.retryFailedDelivery(batchNo);
        return Result.success();
    }
} 