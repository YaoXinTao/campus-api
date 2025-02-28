package com.campus.api.controller.mini;

import com.campus.api.common.Result;
import com.campus.api.dto.banner.BannerDTO;
import com.campus.api.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "小程序轮播图接口", description = "小程序轮播图相关接口")
@RestController
@RequestMapping("/api/v1/mini/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    /**
     * 获取轮播图列表
     */
    @GetMapping("/list")
    public Result<List<BannerDTO>> getBannerList(
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Integer status) {
        List<BannerDTO> list = bannerService.getActiveBanners(position);
        return Result.success(list);
    }
} 