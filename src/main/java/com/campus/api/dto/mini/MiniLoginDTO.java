package com.campus.api.dto.mini;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class MiniLoginDTO {
    @NotBlank(message = "code不能为空")
    private String code;
    
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    
    private String avatarUrl;
    
    private Integer gender;
} 