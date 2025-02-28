package com.campus.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderReviewDTO {
    /**
     * 评分(1-5星)
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
} 