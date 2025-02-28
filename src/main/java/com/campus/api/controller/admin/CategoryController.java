package com.campus.api.controller.admin;

import com.campus.api.entity.Category;
import com.campus.api.service.CategoryService;
import com.campus.api.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台分类管理", description = "后台商品分类管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/category")
@SecurityRequirement(name = "bearer-key")
public class CategoryController {

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

    @Operation(summary = "创建分类", description = "创建新的商品分类")
    @PostMapping("/create")
    public Result<Void> createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return Result.success();
    }

    @Operation(summary = "更新分类", description = "更新商品分类信息")
    @PutMapping("/update")
    public Result<Void> updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @Operation(summary = "删除分类", description = "删除指定的商品分类")
    @Parameter(name = "id", description = "分类ID", required = true)
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "更新分类状态", description = "更新商品分类的状态")
    @PutMapping("/status")
    public Result<Void> updateCategoryStatus(@RequestBody Category category) {
        categoryService.updateCategoryStatus(category.getId(), category.getStatus());
        return Result.success();
    }

    @Operation(summary = "更新分类排序", description = "更新商品分类的排序")
    @PutMapping("/sort")
    public Result<Void> updateCategorySort(@RequestBody Category category) {
        categoryService.updateCategorySort(category.getId(), category.getSortOrder());
        return Result.success();
    }

    @Operation(summary = "更新分类推荐状态", description = "更新商品分类的推荐状态")
    @PutMapping("/featured")
    public Result<Void> updateCategoryFeatured(@RequestBody Category category) {
        categoryService.updateCategoryFeatured(category.getId(), category.getIsFeatured());
        return Result.success();
    }

    @Operation(summary = "获取分类详情", description = "获取指定分类的详细信息")
    @Parameter(name = "id", description = "分类ID", required = true)
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }
} 