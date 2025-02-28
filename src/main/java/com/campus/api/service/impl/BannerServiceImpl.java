package com.campus.api.service.impl;

import com.campus.api.common.PageResult;
import com.campus.api.common.exception.BusinessException;
import com.campus.api.dto.banner.BannerDTO;
import com.campus.api.entity.Banner;
import com.campus.api.mapper.BannerMapper;
import com.campus.api.service.BannerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBanner(BannerDTO bannerDTO, Long adminId) {
        Banner banner = new Banner();
        BeanUtils.copyProperties(bannerDTO, banner);
        banner.setCreatedBy(adminId);
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
        bannerMapper.insert(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBanner(BannerDTO bannerDTO) {
        Banner banner = bannerMapper.selectById(bannerDTO.getId());
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        BeanUtils.copyProperties(bannerDTO, banner);
        banner.setUpdatedAt(LocalDateTime.now());
        bannerMapper.update(banner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        bannerMapper.deleteById(id);
    }

    @Override
    public BannerDTO getBannerDetail(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        BannerDTO dto = new BannerDTO();
        BeanUtils.copyProperties(banner, dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBannerStatus(Long id, Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException("状态值不正确");
        }
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) {
            throw new BusinessException("轮播图不存在");
        }
        bannerMapper.updateStatus(id, status);
    }

    @Override
    public PageResult<BannerDTO> getBannerList(String position, Integer status, Integer pageNum, Integer pageSize) {
        Page<Banner> page = PageHelper.startPage(pageNum, pageSize);
        List<Banner> banners = bannerMapper.selectList(position, status);
        List<BannerDTO> list = banners.stream().map(banner -> {
            BannerDTO dto = new BannerDTO();
            BeanUtils.copyProperties(banner, dto);
            return dto;
        }).collect(Collectors.toList());
        
        return PageResult.of(pageNum, pageSize, page.getTotal(), list);
    }

    @Override
    public List<BannerDTO> getActiveBanners(String position) {
        List<Banner> banners = bannerMapper.selectActiveBanners(position);
        return banners.stream().map(banner -> {
            BannerDTO dto = new BannerDTO();
            BeanUtils.copyProperties(banner, dto);
            return dto;
        }).collect(Collectors.toList());
    }
} 