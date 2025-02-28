package com.campus.api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductReview {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 评价ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 评分(1-5)
     */
    private Integer rating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片JSON字符串
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String images;

    /**
     * 评价图片列表
     */
    @JsonProperty("images")
    public List<String> getImageList() {
        if (images == null || images.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(images, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 商家回复
     */
    private String reply;

    /**
     * 回复时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyTime;

    /**
     * 状态：0-隐藏 1-显示
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 关联信息
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("productImage")
    private String productImage;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("userAvatar")
    private String userAvatar;
    @JsonProperty("userPhone")
    private String userPhone;
} 