package com.ljc.dto;

import lombok.Data;

@Data
public class WorkOrderCreateReq {
    private String title;
    private String content;
    private Long creatorId;
}
