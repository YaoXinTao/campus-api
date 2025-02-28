package com.campus.api.dto.order;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrackingInfo {
    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 物流状态描述
     */
    private String content;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 操作人员
     */
    private String operator;
} 