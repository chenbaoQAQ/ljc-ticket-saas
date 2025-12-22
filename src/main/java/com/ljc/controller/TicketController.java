package com.ljc.controller;

import com.ljc.common.Result;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /** 
     * 兼容旧路径 /tickets
     * 同时提供更规范的 /api/tickets
     */
    @GetMapping({"/tickets", "/api/tickets"})
    public Result<List<Ticket>> listTickets() {
        return Result.success(ticketService.list());
    }
}
