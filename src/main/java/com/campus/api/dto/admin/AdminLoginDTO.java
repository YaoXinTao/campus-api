package com.campus.api.dto.admin;

import lombok.Data;

@Data
public class AdminLoginDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
} 