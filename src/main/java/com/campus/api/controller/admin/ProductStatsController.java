package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.stats.ProductStatsDTO;
import com.campus.api.service.ProductStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品统计", description = "商品统计相关接口")
@RestController
@RequestMapping("/api/v1/admin/product/stats")
@SecurityRequirement(name = "bearer-key")
@RequiredArgsConstructor
public class ProductStatsController {

    private final ProductStatsService productStatsService;

    @Operation(summary = "获取商品统计数据", description = "获取商品统计数据，包括概览、销量趋势、分类占比等")
    @Parameter(name = "type", description = "统计类型：week-近7天 month-近30天", required = true)
    @GetMapping
    public Result<ProductStatsDTO> getProductStats(@RequestParam String type) {
        return Result.success(productStatsService.getProductStats(type));
    }
} 