package com.campus.api.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewListDTO {
    /**
     * 评价ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * SKU规格数据
     */
    private String skuSpecData;

    /**
     * 评分
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片
     */
    private List<String> images;

    /**
     * 是否匿名
     */
    private Boolean isAnonymous;

    /**
     * 商家回复
     */
    private String replyContent;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    /**
     * 评价时间
     */
    private LocalDateTime createdAt;
} 