package com.ljc.vo;

import lombok.Data;

@Data
public class WorkOrderVO {

    private Long id;
    private String title;
    private String content;
    private String status;

    private Long creatorId;
    private Long handlerId;

    private String createTime;
    private String updateTime;
}
