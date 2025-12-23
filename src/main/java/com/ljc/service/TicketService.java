package com.ljc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;

public interface TicketService extends IService<Ticket> {
    Long createTicket(TicketCreateReq req);
}
