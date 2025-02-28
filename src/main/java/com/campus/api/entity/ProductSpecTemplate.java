package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductSpecTemplate {
    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 规格项列表，如[{"name":"颜色","values":["红色","蓝色"]},{"name":"尺寸","values":["S","M","L"]}]
     */
    private String specItems;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}