package com.campus.api.dto.product;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品简介
     */
    private String brief;

    /**
     * 商品关键字
     */
    private String keywords;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 商品相册
     */
    private List<String> album;

    /**
     * 商品单位
     */
    private String unit;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 状态：0-下架 1-上架
     */
    private Integer status;

    /**
     * 是否推荐：0-否 1-是
     */
    private Integer isFeatured;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 富文本详情
     */
    private String richContent;

    /**
     * 规格参数描述
     */
    private String specDesc;

    /**
     * 包装清单
     */
    private String packingList;

    /**
     * 售后服务
     */
    private String serviceNotes;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 总销量
     */
    private Integer totalSales;

    /**
     * 审核状态：0-未审核 1-审核通过 2-审核不通过
     */
    private Integer verifyStatus;

    /**
     * SKU列表
     */
    private List<ProductSkuDTO> skuList;
} 