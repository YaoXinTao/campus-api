package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.order.*;
import com.campus.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员换货接口", description = "后台换货管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/exchange")
@RequiredArgsConstructor
public class AdminExchangeController {

    private final OrderService orderService;

    @Operation(summary = "获取换货详情")
    @GetMapping("/{id}")
    public Result<ExchangeDetailDTO> getExchangeDetail(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        return Result.success(orderService.getExchangeDetailForAdmin(id));
    }

    @Operation(summary = "同意换货")
    @PostMapping("/{id}/agree")
    public Result<Void> agreeExchange(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        orderService.agreeExchange(id);
        return Result.success();
    }

    @Operation(summary = "确认收到退回商品")
    @PostMapping("/{id}/confirm-return")
    public Result<Void> confirmReturn(
            @Parameter(description = "换货记录ID") @PathVariable Long id) {
        orderService.confirmReturn(id);
        return Result.success();
    }

    @Operation(summary = "拒绝换货")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectExchange(
            @Parameter(description = "换货记录ID") @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam String reason) {
        orderService.rejectExchange(id, reason);
        return Result.success();
    }

    @Operation(summary = "发货")
    @PostMapping("/{id}/send")
    public Result<Void> sendExchange(
            @Parameter(description = "换货记录ID") @PathVariable Long id,
            @RequestBody @Valid OrderDeliveryDTO deliveryDTO) {
        orderService.sendExchangeGoods(id, deliveryDTO.getDeliveryCompany(), deliveryDTO.getDeliveryNo());
        return Result.success();
    }

    @Operation(summary = "获取换货列表")
    @GetMapping("/list")
    public Result<PageResult<ExchangeDetailDTO>> getExchangeList(
            @Parameter(description = "换货状态：0-待处理 1-已同意 2-已拒绝 3-待寄回 4-待发货 5-已发货 6-已完成") 
            @RequestParam(required = false) Integer status,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单编号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "换货单号") @RequestParam(required = false) String exchangeNo,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(orderService.getExchangeListForAdmin(pageNum, pageSize, status, userId, orderNo, exchangeNo));
    }
} 