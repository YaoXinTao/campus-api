package com.campus.api.controller.mini;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.order.AddressDTO;
import com.campus.api.dto.order.ExchangeApplyDTO;
import com.campus.api.dto.order.ExchangeDeliveryDTO;
import com.campus.api.dto.order.ExchangeDetailDTO;
import com.campus.api.dto.order.TrackingInfo;
import com.campus.api.service.OrderService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序换货接口", description = "小程序端换货相关接口")
@RestController
@RequestMapping("/api/v1/mini/exchange")
@RequiredArgsConstructor
public class MiniExchangeController {

    private final OrderService orderService;

    /**
     * 申请换货
     */
    @Operation(summary = "申请换货")
    @PostMapping("/apply")
    public Result<Void> applyExchange(@RequestBody @Valid ExchangeApplyDTO exchangeApplyDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.applyExchange(userId, exchangeApplyDTO);
        return Result.success();
    }

    /**
     * 取消换货申请
     */
    @Operation(summary = "取消换货")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelExchange(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelExchange(userId, id);
        return Result.success();
    }

    /**
     * 填写退货物流信息
     */
    @Operation(summary = "寄回商品")
    @PostMapping("/return")
    public Result<Void> returnGoods(@RequestBody @Valid ExchangeDeliveryDTO deliveryDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.returnGoods(userId, deliveryDTO.getExchangeId(), deliveryDTO);
        return Result.success();
    }

    /**
     * 确认收到换货商品
     */
    @Operation(summary = "确认换货")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmExchange(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.confirmExchangeReceive(userId, id);
        return Result.success();
    }

    /**
     * 更新换货收货地址
     */
    @Operation(summary = "更新换货地址")
    @PostMapping("/{id}/address")
    public Result<Void> updateExchangeAddress(
            @Parameter(description = "换货记录ID") @PathVariable Long id,
            @RequestBody @Valid AddressDTO addressDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.updateExchangeAddress(userId, id, addressDTO.getId());
        return Result.success();
    }

    /**
     * 获取换货详情
     */
    @Operation(summary = "获取换货详情")
    @GetMapping("/{id}")
    public Result<ExchangeDetailDTO> getExchangeDetail(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.getExchangeDetail(userId, id));
    }

    /**
     * 获取换货列表
     */
    @Operation(summary = "获取换货列表")
    @GetMapping("/list")
    public Result<PageResult<ExchangeDetailDTO>> getExchangeList(
            @Parameter(description = "换货状态：0-待处理 1-已同意 2-已拒绝 3-待寄回 4-待发货 5-已发货 6-已完成") 
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.getExchangeList(userId, pageNum, pageSize, status));
    }

    /**
     * 获取物流跟踪信息
     */
    @Operation(summary = "查询物流信息")
    @GetMapping("/delivery/{id}/track")
    public Result<List<TrackingInfo>> getDeliveryTracking(
            @Parameter(description = "换货记录ID") @PathVariable Long id,
            @Parameter(description = "物流类型：1-退货物流 2-换货物流，不传则返回所有") 
            @RequestParam(required = false) Integer type) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.getExchangeDeliveryTracking(userId, id, type));
    }
} 