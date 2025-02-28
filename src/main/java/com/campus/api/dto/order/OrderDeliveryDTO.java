package com.campus.api.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "订单发货信息")
public class OrderDeliveryDTO {
    
    @Schema(description = "物流公司")
    @NotBlank(message = "物流公司不能为空")
    private String deliveryCompany;
    
    @Schema(description = "物流单号")
    @NotBlank(message = "物流单号不能为空")
    private String deliveryNo;
    
    @Schema(description = "备注")
    private String remark;
} 