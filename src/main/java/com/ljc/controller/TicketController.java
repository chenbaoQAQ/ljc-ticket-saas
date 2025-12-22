package com.ljc.controller;

import com.ljc.common.Result;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // 2. 将返回类型从 List<String> 改为 List<Ticket> ✅
    @GetMapping("/tickets")
    public List<Ticket> getTickets() {
        // 现在这里返回的是从数据库查出来的完整工单对象列表
        return ticketService.getAllTickets();
    }
}