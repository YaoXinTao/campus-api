package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusinessRule {
    private Long id;
    private String ruleType;
    private String ruleKey;
    private String ruleValue;
    private String ruleDesc;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 