package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.ProductTagDTO;
import com.campus.api.service.ProductTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序-商品标签接口")
@RestController
@RequestMapping("/mini/product/tag")
public class MiniProductTagController {

    @Autowired
    private ProductTagService productTagService;

    @Operation(summary = "获取商品的标签列表")
    @GetMapping("/list/{productId}")
    public Result<List<ProductTagDTO>> getProductTags(@PathVariable Long productId) {
        List<ProductTagDTO> list = productTagService.getTagsByProductId(productId);
        return Result.success(list);
    }

    @Operation(summary = "获取所有启用的标签")
    @GetMapping("/list/enabled")
    public Result<List<ProductTagDTO>> listEnabled() {
        List<ProductTagDTO> list = productTagService.getAllEnabledTags();
        return Result.success(list);
    }
} 