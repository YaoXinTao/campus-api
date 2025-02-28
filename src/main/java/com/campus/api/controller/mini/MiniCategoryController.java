package com.campus.api.controller.mini;

import com.campus.api.entity.Category;
import com.campus.api.service.CategoryService;
import com.campus.api.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序分类接口", description = "小程序商品分类相关接口")
@RestController
@RequestMapping("/api/v1/mini/category")
public class MiniCategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "获取分类列表", description = "获取所有商品分类列表")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.getCategoryList();
        return Result.success(categories);
    }

    @Operation(summary = "获取推荐分类", description = "获取推荐的商品分类列表")
    @GetMapping("/featured")
    public Result<List<Category>> getFeaturedCategories() {
        List<Category> categories = categoryService.getFeaturedCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取子分类", description = "根据父分类ID获取子分类列表")
    @Parameter(name = "parentId", description = "父分类ID", required = true)
    @GetMapping("/sub/{parentId}")
    public Result<List<Category>> getSubCategories(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getSubCategories(parentId);
        return Result.success(categories);
    }

    @Operation(summary = "获取分类详情", description = "获取指定分类的详细信息")
    @Parameter(name = "id", description = "分类ID", required = true)
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }
} 