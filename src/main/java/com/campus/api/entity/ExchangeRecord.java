package com.campus.api.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExchangeRecord {
    /**
     * 换货记录ID
     */
    private Long id;

    /**
     * 换货单号
     */
    private String exchangeNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单商品ID
     */
    private Long orderItemId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 原SKU ID
     */
    private Long oldSkuId;

    /**
     * 新SKU ID
     */
    private Long newSkuId;

    /**
     * 原SKU规格数据
     */
    private String oldSkuSpecData;

    /**
     * 新SKU规格数据
     */
    private String newSkuSpecData;

    /**
     * 换货原因类型：1-尺码不合适 2-颜色与描述不符 3-款式与描述不符 4-其他
     */
    private Integer exchangeReasonType;

    /**
     * 换货原因
     */
    private String exchangeReason;

    /**
     * 换货状态：0-待处理 1-已同意 2-已拒绝 3-待买家退货 4-已退货待确认 5-待发货 6-已发货 7-已完成
     */
    private Integer exchangeStatus;

    /**
     * 拒绝原因
     */
    private String rejectReason;

    /**
     * 拒绝时间
     */
    private LocalDateTime rejectTime;

    /**
     * 凭证图片
     */
    private String evidenceImages;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 