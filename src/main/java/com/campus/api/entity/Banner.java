package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Banner {
    private Long id;
    private String title;
    private String imageUrl;
    private Integer linkType;
    private String linkValue;
    private String position;
    private Integer sortOrder;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 