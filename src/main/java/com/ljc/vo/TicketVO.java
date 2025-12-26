package com.ljc.vo;

import lombok.Data;

@Data
public class TicketVO {
    private Long id;
    private Long workOrderId;
    private String description;
    private String createTime; // 先用 String，后面再统一格式化
    private String updateTime;
}
