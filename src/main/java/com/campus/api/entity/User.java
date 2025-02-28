package com.campus.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class User {
    private Long id;
    
    @JsonIgnore
    private String openid;
    
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    
    private String avatarUrl;
    private Integer gender;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Pattern(regexp = "^\\d{8,12}$", message = "学号格式不正确")
    private String studentId;
    
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    private String college;
    private String major;
    private String grade;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @JsonIgnore
    private transient String token; // 不持久化到数据库的token字段
} 