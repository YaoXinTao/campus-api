package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.common.PageResult;
import com.campus.api.dto.ProductTagDTO;
import com.campus.api.entity.ProductTag;
import com.campus.api.service.ProductTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "商品标签管理")
@RestController
@RequestMapping("/api/v1/admin/product/tag")
@RequiredArgsConstructor
public class ProductTagController {

    private final ProductTagService productTagService;

    @Operation(summary = "添加标签")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductTagDTO productTagDTO) {
        productTagService.add(productTagDTO);
        return Result.success();
    }

    @Operation(summary = "修改标签")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProductTagDTO productTagDTO) {
        productTagService.update(id, productTagDTO);
        return Result.success();
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productTagService.delete(id);
        return Result.success();
    }

    @Operation(summary = "获取标签详情")
    @GetMapping("/{id}")
    public Result<ProductTag> getDetail(@PathVariable Long id) {
        return Result.success(productTagService.getById(id));
    }

    @Operation(summary = "分页获取标签列表")
    @GetMapping("/list")
    public Result<PageResult<ProductTag>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(productTagService.getList(keyword, pageNum, pageSize));
    }

    @Operation(summary = "批量设置商品标签")
    @PostMapping("/batch/{tagId}")
    public Result<Void> batchSetTags(
            @PathVariable Long tagId,
            @RequestBody List<Long> productIds) {
        productTagService.batchSetProductTags(tagId, productIds);
        return Result.success();
    }

    @Operation(summary = "获取所有启用的标签")
    @GetMapping("/list/enabled")
    public Result<List<ProductTagDTO>> listEnabled() {
        List<ProductTagDTO> list = productTagService.getAllEnabledTags();
        return Result.success(list);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productTagService.updateStatus(id, status);
        return Result.success();
    }
} 