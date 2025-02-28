package com.campus.api.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "商品评价DTO")
public class ProductReviewDTO {
    /**
     * 评价ID
     */
    @Schema(description = "评价ID")
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String userNickname;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userAvatar;

    /**
     * 商品ID
     */
    @Schema(description = "商品ID")
    private Long productId;

    /**
     * 订单ID
     */
    @Schema(description = "订单ID")
    private Long orderId;

    /**
     * SKU ID
     */
    @Schema(description = "SKU ID")
    private Long skuId;

    /**
     * 评分(1-5)
     */
    @Schema(description = "评分(1-5)")
    private Integer rating;

    /**
     * 评价内容
     */
    @Schema(description = "评价内容")
    private String content;

    /**
     * 评价图片
     */
    @Schema(description = "评价图片")
    private List<String> images;

    /**
     * 商家回复
     */
    @Schema(description = "商家回复")
    private String reply;

    /**
     * 回复时间
     */
    @Schema(description = "回复时间")
    private LocalDateTime replyTime;

    /**
     * 状态：0-隐藏 1-显示
     */
    @Schema(description = "状态：0-隐藏 1-显示")
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

    // 关联信息
    @Schema(description = "商品名称")
    private String productName;
    @Schema(description = "商品图片")
    private String productImage;
    @Schema(description = "用户名")
    private String userName;
    @Schema(description = "用户电话")
    private String userPhone;
} 