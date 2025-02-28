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

import java.time.LocalDateTime;

@Tag(name = "管理员订单接口", description = "后台订单管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        return Result.success(orderService.getOrderDetail(id));
    }

    @Operation(summary = "发货")
    @PostMapping("/{id}/deliver")
    public Result<Void> deliverOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestBody @Valid OrderDeliveryDTO deliveryDTO) {
        orderService.deliver(id, deliveryDTO.getDeliveryCompany(), deliveryDTO.getDeliveryNo());
        return Result.success();
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<PageResult<OrderDetailDTO>> getOrderList(
            @Parameter(description = "订单编号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款") 
            @RequestParam(required = false) Integer orderStatus,
            @Parameter(description = "支付状态：0-未支付 1-已支付 2-已退款") 
            @RequestParam(required = false) Integer paymentStatus,
            @Parameter(description = "发货状态：0-未发货 1-已发货 2-已收货") 
            @RequestParam(required = false) Integer deliveryStatus,
            @Parameter(description = "收货人姓名") @RequestParam(required = false) String receiver,
            @Parameter(description = "收货人手机号") @RequestParam(required = false) String phone,
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setOrderNo(orderNo);
        queryDTO.setOrderStatus(orderStatus);
        queryDTO.setPaymentStatus(paymentStatus);
        queryDTO.setDeliveryStatus(deliveryStatus);
        queryDTO.setReceiver(receiver);
        queryDTO.setPhone(phone);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        if (startTime != null) {
            queryDTO.setStartTime(LocalDateTime.parse(startTime.replace(" ", "T")));
        }
        if (endTime != null) {
            queryDTO.setEndTime(LocalDateTime.parse(endTime.replace(" ", "T")));
        }
        return Result.success(orderService.getOrderList(queryDTO));
    }

    @Operation(summary = "同意退款")
    @PostMapping("/refund/{id}/agree")
    public Result<Void> agreeRefund(
            @Parameter(description = "退款记录ID") @PathVariable Long id) {
        orderService.agreeRefund(id);
        return Result.success();
    }

    @Operation(summary = "拒绝退款")
    @PostMapping("/refund/{id}/reject")
    public Result<Void> rejectRefund(
            @Parameter(description = "退款记录ID") @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam String reason) {
        orderService.rejectRefund(id, reason);
        return Result.success();
    }
} 