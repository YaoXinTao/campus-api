package com.campus.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "地址信息")
public class AddressDTO {
    
    @Schema(description = "地址ID")
    private Long id;
    
    @Schema(description = "收货人姓名")
    @NotBlank(message = "收货人姓名不能为空")
    private String receiver;
    
    @Schema(description = "收货人手机号")
    @NotBlank(message = "收货人手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "省份")
    @NotBlank(message = "省份不能为空")
    private String province;
    
    @Schema(description = "城市")
    @NotBlank(message = "城市不能为空")
    private String city;
    
    @Schema(description = "区县")
    @NotBlank(message = "区县不能为空")
    private String district;
    
    @Schema(description = "详细地址")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;
    
    @Schema(description = "是否默认地址")
    private Boolean isDefault;
}