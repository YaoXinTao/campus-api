package com.campus.api.controller.mini;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.order.CreateOrderDTO;
import com.campus.api.dto.order.OrderDetailDTO;
import com.campus.api.dto.order.OrderQueryDTO;
import com.campus.api.dto.order.RefundApplyDTO;
import com.campus.api.service.OrderService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "小程序订单接口", description = "小程序端订单相关接口")
@RestController
@RequestMapping("/api/v1/mini/order")
@RequiredArgsConstructor
public class MiniOrderController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<Long> createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.createOrder(userId, createOrderDTO));
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailDTO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        // 先检查订单是否属于当前用户
        OrderDetailDTO orderDetail = orderService.getOrderDetail(id);
        if (!userId.equals(orderDetail.getUserId())) {
            throw new BusinessException("无权查看此订单");
        }
        return Result.success(orderDetail);
    }

    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<PageResult<OrderDetailDTO>> getOrderList(
            @Parameter(description = "订单状态：10-待付款 20-待发货 30-待收货 40-已完成 50-已取消 60-已退款") 
            @RequestParam(required = false) Integer orderStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        OrderQueryDTO queryDTO = new OrderQueryDTO();
        queryDTO.setUserId(userId);
        queryDTO.setOrderStatus(orderStatus);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return Result.success(orderService.getOrderList(queryDTO));
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "取消原因") @RequestParam String reason) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, id, reason);
        return Result.success();
    }

    /**
     * 支付订单
     */
    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<Object> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "支付方式：1-微信支付 2-支付宝") @RequestParam Integer paymentMethod) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.payOrder(userId, id, paymentMethod));
    }

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货")
    @PostMapping("/{id}/confirm")
    public Result<Void> confirmReceive(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.confirmReceive(userId, id);
        return Result.success();
    }

    /**
     * 申请退款
     */
    @Operation(summary = "申请退款")
    @PostMapping("/refund/apply")
    public Result<Void> applyRefund(@RequestBody @Valid RefundApplyDTO refundApplyDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.applyRefund(userId, refundApplyDTO);
        return Result.success();
    }

    /**
     * 删除订单
     */
    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public Result<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.deleteOrder(userId, id);
        return Result.success();
    }
} 