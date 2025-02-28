package com.campus.api.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Schema(description = "地址DTO")
public class AddressDTO {
    @Schema(description = "地址ID")
    private Long id;
    
    @Schema(description = "收货人姓名", required = true)
    @NotBlank(message = "收货人姓名不能为空")
    private String receiver;
    
    @Schema(description = "收货人手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @Schema(description = "省份", required = true)
    @NotBlank(message = "省份不能为空")
    private String province;
    
    @Schema(description = "城市", required = true)
    @NotBlank(message = "城市不能为空")
    private String city;
    
    @Schema(description = "区/县", required = true)
    @NotBlank(message = "区/县不能为空")
    private String district;
    
    @Schema(description = "详细地址", required = true)
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;
    
    @Schema(description = "邮政编码")
    private String postalCode;
    
    @Schema(description = "是否默认地址 0-否 1-是")
    private Integer isDefault;
    
    @Schema(description = "地址标签：家、学校、公司等")
    private String tag;
} 