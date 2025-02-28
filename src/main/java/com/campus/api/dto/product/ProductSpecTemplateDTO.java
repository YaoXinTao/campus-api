package com.campus.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品规格模板DTO")
public class ProductSpecTemplateDTO {
    /**
     * 模板ID
     */
    @Schema(description = "模板ID")
    private Long id;

    /**
     * 模板名称
     */
    @Schema(description = "模板名称")
    private String name;

    /**
     * 规格项列表
     */
    @Schema(description = "规格项列表")
    private List<SpecItem> specItems;

    /**
     * 状态：0-禁用 1-启用
     */
    @Schema(description = "状态：0-禁用 1-启用")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Data
    @Schema(description = "规格项")
    public static class SpecItem {
        /**
         * 规格项名称
         */
        @Schema(description = "规格项名称")
        private String name;

        /**
         * 规格值列表
         */
        @Schema(description = "规格值列表")
        private List<String> values;
    }
} 