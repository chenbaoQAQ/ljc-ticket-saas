package com.ljc.dto;

import lombok.Data;

@Data
public class TicketCreateReq {
    private Long workOrderId;
    private String description;
}
