package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {
    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标
     */
    private String iconUrl;

    /**
     * 分类banner图URL
     */
    private String bannerUrl;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类关键词
     */
    private String keywords;

    /**
     * 父分类ID，0表示一级分类
     */
    private Long parentId;

    /**
     * 分类层级 1-一级分类 2-二级分类
     */
    private Integer level;

    /**
     * 排序号，越小越靠前
     */
    private Integer sortOrder;

    /**
     * 是否推荐 0-否 1-是
     */
    private Integer isFeatured;

    /**
     * 状态 0-禁用 1-正常
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 