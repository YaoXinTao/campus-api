package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.dto.ProductAttributeDTO;
import com.campus.api.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "商品属性管理")
@RestController
@RequestMapping("/admin/product/attribute")
public class ProductAttributeController {

    @Autowired
    private ProductAttributeService productAttributeService;

    @Operation(summary = "添加属性")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ProductAttributeDTO dto) {
        productAttributeService.addAttribute(dto);
        return Result.success();
    }

    @Operation(summary = "修改属性")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ProductAttributeDTO dto) {
        productAttributeService.updateAttribute(dto);
        return Result.success();
    }

    @Operation(summary = "删除属性")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productAttributeService.deleteAttribute(id);
        return Result.success();
    }

    @Operation(summary = "获取属性列表")
    @GetMapping("/list/{productId}")
    public Result<List<ProductAttributeDTO>> list(@PathVariable Long productId) {
        List<ProductAttributeDTO> list = productAttributeService.getAttributesByProductId(productId);
        return Result.success(list);
    }

    @Operation(summary = "批量添加商品属性")
    @PostMapping("/batch/{productId}")
    public Result<Void> batchAdd(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductAttributeDTO> attributes) {
        productAttributeService.batchAddAttributes(productId, attributes);
        return Result.success();
    }
} 