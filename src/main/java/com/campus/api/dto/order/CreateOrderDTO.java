package com.campus.api.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderDTO {
    /**
     * 收货地址ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /**
     * 收货人
     */
    @NotNull(message = "收货人不能为空")
    private String receiver;

    /**
     * 联系电话
     */
    @NotNull(message = "联系电话不能为空")
    private String phone;

    /**
     * 省份
     */
    @NotNull(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotNull(message = "城市不能为空")
    private String city;

    /**
     * 区县
     */
    @NotNull(message = "区县不能为空")
    private String district;

    /**
     * 详细地址
     */
    @NotNull(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 运费金额
     */
    private BigDecimal freightAmount;

    /**
     * 商品列表
     */
    @NotEmpty(message = "商品不能为空")
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;

        /**
         * SKU ID
         */
        @NotNull(message = "SKU ID不能为空")
        private Long skuId;

        /**
         * 商品名称
         */
        @NotNull(message = "商品名称不能为空")
        private String productName;

        /**
         * 商品图片
         */
        @NotNull(message = "商品图片不能为空")
        private String productImage;

        /**
         * SKU规格数据
         */
        private String skuSpec;

        /**
         * 商品单价
         */
        @NotNull(message = "商品单价不能为空")
        private BigDecimal price;

        /**
         * 购买数量
         */
        @NotNull(message = "购买数量不能为空")
        private Integer quantity;
    }
} 