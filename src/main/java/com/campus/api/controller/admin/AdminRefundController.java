package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.RefundDetailDTO;
import com.campus.api.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员退款接口", description = "后台退款管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/refund")
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundService refundService;

    @Operation(summary = "获取退款详情")
    @GetMapping("/{id}")
    public Result<RefundDetailDTO> getRefundDetail(
            @Parameter(description = "退款记录ID") @PathVariable Long id) {
        return Result.success(refundService.getRefundDetail(id));
    }

    @Operation(summary = "同意退款")
    @PostMapping("/{id}/agree")
    public Result<Void> agreeRefund(
            @Parameter(description = "退款记录ID") @PathVariable Long id) {
        refundService.processRefund(id, true, null);
        return Result.success();
    }

    @Operation(summary = "拒绝退款")
    @PostMapping("/{id}/reject")
    public Result<Void> rejectRefund(
            @Parameter(description = "退款记录ID") @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam String reason) {
        refundService.processRefund(id, false, reason);
        return Result.success();
    }

    @Operation(summary = "获取退款列表")
    @GetMapping("/list")
    public Result<PageResult<RefundDetailDTO>> getRefundList(
            @Parameter(description = "退款状态：0-待处理 1-已同意 2-已拒绝 3-已完成") 
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(refundService.getMerchantRefunds(status, pageNum, pageSize));
    }
} 