package com.ljc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;

public interface TicketService extends IService<Ticket> {

    Long createTicket(Long companyId, TicketCreateReq req);//Long companyId保证了这个历史记录只能由这个公司创建

    Page<Ticket> pageByWorkOrder(Long companyId, Long workOrderId, long page, long size);

    Ticket getByIdWithCompany(Long companyId, Long ticketId);

}
