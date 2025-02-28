package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private String avatarUrl;
    private String role;
    private String department;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private transient String token;
} 