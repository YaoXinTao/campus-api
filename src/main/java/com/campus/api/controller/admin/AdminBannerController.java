package com.campus.api.controller.admin;

import com.campus.api.common.PageResult;
import com.campus.api.common.Result;
import com.campus.api.dto.banner.BannerDTO;
import com.campus.api.service.BannerService;
import com.campus.api.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台轮播图接口", description = "管理后台轮播图相关接口")
@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @Operation(summary = "创建轮播图")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> createBanner(@Validated @RequestBody BannerDTO bannerDTO) {
        Long adminId = SecurityUtils.getCurrentUserId();
        bannerService.createBanner(bannerDTO, adminId);
        return Result.success();
    }

    @Operation(summary = "更新轮播图")
    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> updateBanner(@Validated @RequestBody BannerDTO bannerDTO) {
        bannerService.updateBanner(bannerDTO);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return Result.success();
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<BannerDTO> getBannerDetail(@PathVariable Long id) {
        return Result.success(bannerService.getBannerDetail(id));
    }

    @Operation(summary = "更新轮播图状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<Void> updateStatus(
            @Parameter(description = "轮播图ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-禁用 1-启用", required = true) @RequestParam Integer status) {
        bannerService.updateBannerStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public Result<PageResult<BannerDTO>> getBannerList(
            @Parameter(description = "展示位置：HOME-首页 CATEGORY-分类页") @RequestParam(required = false) String position,
            @Parameter(description = "状态：0-禁用 1-启用") @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(bannerService.getBannerList(position, status, pageNum, pageSize));
    }
} 