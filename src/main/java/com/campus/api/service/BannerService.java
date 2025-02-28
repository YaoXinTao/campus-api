package com.campus.api.service;

import com.campus.api.common.PageResult;
import com.campus.api.dto.banner.BannerDTO;
import java.util.List;

public interface BannerService {
    // 创建轮播图
    void createBanner(BannerDTO bannerDTO, Long adminId);

    // 更新轮播图
    void updateBanner(BannerDTO bannerDTO);

    // 删除轮播图
    void deleteBanner(Long id);

    // 获取轮播图详情
    BannerDTO getBannerDetail(Long id);

    // 更新轮播图状态
    void updateBannerStatus(Long id, Integer status);

    // 获取轮播图列表(管理端)
    PageResult<BannerDTO> getBannerList(String position, Integer status, Integer pageNum, Integer pageSize);

    // 获取轮播图列表(小程序端)
    List<BannerDTO> getActiveBanners(String position);
} 