package com.campus.api.dto.banner;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "轮播图DTO")
public class BannerDTO {
    @Schema(description = "轮播图ID")
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Schema(description = "轮播图标题", required = true)
    private String title;

    @NotBlank(message = "图片URL不能为空")
    @Schema(description = "图片URL", required = true)
    private String imageUrl;

    @NotNull(message = "链接类型不能为空")
    @Schema(description = "链接类型：1-商品 2-分类 3-外部链接 4-无链接", required = true)
    private Integer linkType;

    @Schema(description = "链接值：商品ID/分类ID/外部URL")
    private String linkValue;

    @Schema(description = "展示位置：HOME-首页 CATEGORY-分类页", defaultValue = "HOME")
    private String position = "HOME";

    @Schema(description = "排序号，越小越靠前", defaultValue = "0")
    private Integer sortOrder = 0;

    @Schema(description = "开始展示时间")
    private LocalDateTime startTime;

    @Schema(description = "结束展示时间")
    private LocalDateTime endTime;

    @Schema(description = "状态：0-禁用 1-启用", defaultValue = "1")
    private Integer status = 1;

    @Schema(description = "备注")
    private String remark;
} 