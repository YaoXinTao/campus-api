package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.product.ProductSpecTemplateDTO;
import com.campus.api.service.ProductSpecTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品规格管理", description = "商品规格模板相关接口")
@RestController
@RequestMapping("/api/v1/admin/product/spec")
public class ProductSpecController {

    @Autowired
    private ProductSpecTemplateService templateService;

    @Operation(summary = "获取规格模板详情")
    @GetMapping("/template/{id}")
    public Result<ProductSpecTemplateDTO> getTemplate(@PathVariable Long id) {
        return Result.success(templateService.getTemplateById(id));
    }

    @Operation(summary = "获取规格模板列表")
    @GetMapping("/template/list")
    public Result<List<ProductSpecTemplateDTO>> getTemplateList() {
        return Result.success(templateService.getTemplateList());
    }

    @Operation(summary = "创建规格模板")
    @PostMapping("/template")
    public Result<Void> createTemplate(@RequestBody ProductSpecTemplateDTO templateDTO) {
        templateService.createTemplate(templateDTO);
        return Result.success();
    }

    @Operation(summary = "更新规格模板")
    @PutMapping("/template")
    public Result<Void> updateTemplate(@RequestBody ProductSpecTemplateDTO templateDTO) {
        templateService.updateTemplate(templateDTO);
        return Result.success();
    }

    @Operation(summary = "删除规格模板")
    @DeleteMapping("/template/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return Result.success();
    }

    @Operation(summary = "更新规格模板状态")
    @PutMapping("/template/{id}/status")
    public Result<Void> updateTemplateStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        templateService.updateTemplateStatus(id, status);
        return Result.success();
    }
}