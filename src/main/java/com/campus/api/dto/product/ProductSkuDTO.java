package com.campus.api.dto.product;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ProductSkuDTO {
    /**
     * SKU ID
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * SKU编码
     */
    private String skuCode;

    /**
     * 规格数据
     */
    private List<SpecData> specData;

    /**
     * 销售价
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * SKU图片
     */
    private String imageUrl;

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

    @Data
    public static class SpecData {
        /**
         * 规格项名称
         */
        private String key;

        /**
         * 规格值
         */
        private String value;
    }
} 