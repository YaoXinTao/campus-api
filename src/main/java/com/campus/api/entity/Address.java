package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;
    private Long userId;
    private String receiver;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private String postalCode;
    private Integer isDefault;
    private String tag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 