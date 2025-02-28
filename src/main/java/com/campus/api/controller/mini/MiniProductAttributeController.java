package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.ProductAttributeDTO;
import com.campus.api.service.ProductAttributeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序-商品属性接口")
@RestController
@RequestMapping("/mini/product/attribute")
public class MiniProductAttributeController {

    @Autowired
    private ProductAttributeService productAttributeService;

    @Operation(summary = "获取商品属性列表")
    @GetMapping("/list/{productId}")
    public Result<List<ProductAttributeDTO>> list(@PathVariable Long productId) {
        List<ProductAttributeDTO> list = productAttributeService.getAttributesByProductId(productId);
        return Result.success(list);
    }
} 