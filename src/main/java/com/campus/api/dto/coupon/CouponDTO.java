package com.campus.api.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Future;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "优惠券DTO")
public class CouponDTO {
    @Schema(description = "优惠券ID")
    private Long id;

    @Schema(description = "优惠券名称", required = true)
    @NotBlank(message = "优惠券名称不能为空")
    private String name;

    @Schema(description = "优惠券类型：1-满减券 2-折扣券 3-无门槛券", required = true)
    @NotNull(message = "优惠券类型不能为空")
    private Integer type;

    @Schema(description = "优惠金额/折扣率", required = true)
    @NotNull(message = "优惠金额/折扣率不能为空")
    @Min(value = 0, message = "优惠金额/折扣率必须大于等于0")
    private BigDecimal amount;

    @Schema(description = "最低消费金额")
    private BigDecimal minSpend;

    @Schema(description = "适用分类ID，空表示全场通用")
    private Long categoryId;

    @Schema(description = "生效时间", required = true)
    @NotNull(message = "生效时间不能为空")
    @Future(message = "生效时间必须是未来时间")
    private LocalDateTime startTime;

    @Schema(description = "失效时间", required = true)
    @NotNull(message = "失效时间不能为空")
    @Future(message = "失效时间必须是未来时间")
    private LocalDateTime endTime;

    @Schema(description = "发行总量", required = true)
    @NotNull(message = "发行总量不能为空")
    @Min(value = 1, message = "发行总量必须大于0")
    private Integer totalCount;

    @Schema(description = "剩余数量")
    private Integer remainCount;

    @Schema(description = "每人限领数量")
    @Min(value = 1, message = "每人限领数量必须大于0")
    private Integer perLimit;

    @Schema(description = "使用说明")
    private String description;

    @Schema(description = "状态：0-已停用 1-正常")
    private Integer status;
} 