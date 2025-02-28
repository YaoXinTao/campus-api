package com.campus.api.controller.admin;

import com.campus.api.common.Result;
import com.campus.api.common.PageResult;
import com.campus.api.dto.product.ProductDTO;
import com.campus.api.dto.product.ProductQuery;
import com.campus.api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "后台商品管理", description = "后台商品管理相关接口")
@RestController
@RequestMapping("/api/v1/admin/product")
@SecurityRequirement(name = "bearer-key")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取商品详情", description = "根据ID获取商品详情")
    @Parameter(name = "id", description = "商品ID", required = true)
    @GetMapping("/{id}")
    public Result<ProductDTO> getProduct(@PathVariable Long id) {
        return Result.success(productService.getProductById(id));
    }

    @Operation(summary = "获取商品列表", description = "获取商品列表，支持分页和条件查询")
    @GetMapping("/list")
    public Result<PageResult<ProductDTO>> getProductList(ProductQuery query) {
        return Result.success(productService.getProductList(query));
    }

    @Operation(summary = "创建商品", description = "创建新商品")
    @PostMapping("/create")
    public Result<Void> createProduct(@RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return Result.success();
    }

    @Operation(summary = "更新商品", description = "更新商品信息")
    @PutMapping("/update")
    public Result<Void> updateProduct(@RequestBody ProductDTO productDTO) {
        productService.updateProduct(productDTO);
        return Result.success();
    }

    @Operation(summary = "删除商品", description = "根据ID删除商品")
    @Parameter(name = "id", description = "商品ID", required = true)
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    @Operation(summary = "上传商品图片", description = "上传商品主图或相册图")
    @PostMapping("/upload")
    public Result<String> uploadImage(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "图片类型：main-主图 album-相册图", required = true) @RequestParam("type") String type) {
        return Result.success(productService.uploadImage(file, type));
    }

    @Operation(summary = "更新商品状态", description = "更新商品上下架状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Parameter(description = "状态：0-下架 1-上架", required = true) @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "更新审核状态", description = "更新商品审核状态")
    @PutMapping("/{id}/verify")
    public Result<Void> updateVerifyStatus(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Parameter(description = "审核状态：0-未审核 1-审核通过 2-审核不通过", required = true) @RequestParam Integer verifyStatus) {
        productService.updateVerifyStatus(id, verifyStatus);
        return Result.success();
    }

    @Operation(summary = "更新推荐状态", description = "更新商品推荐状态")
    @PutMapping("/{id}/featured")
    public Result<Void> updateFeatured(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Parameter(description = "是否推荐：0-否 1-是", required = true) @RequestParam Integer isFeatured) {
        productService.updateFeatured(id, isFeatured);
        return Result.success();
    }
} 