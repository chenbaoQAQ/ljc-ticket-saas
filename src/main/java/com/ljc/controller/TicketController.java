package com.ljc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljc.common.Result;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping({"/tickets", "/api/tickets"})
    public Result<List<Ticket>> listTickets() {
        return Result.success(ticketService.list());
    }

    @PostMapping("/api/tickets")
    public Result<Long> createTicket(@RequestBody TicketCreateReq req) {
        Long id = ticketService.createTicket(req);
        return Result.success(id);
    }
    @GetMapping("api/tickets/by-work-order")
    public Result<List<Ticket>> getById(@RequestParam Long workOrderId){
        /*
        qw.eq(...)      // 等于
        qw.like(...)    // 模糊查询
        qw.orderByDesc // 排序
         */
        LambdaQueryWrapper<Ticket> qw = new LambdaQueryWrapper<>();
        qw.eq(Ticket::getWorkOrderId, workOrderId);//Ticket=Work Orderid

        return Result.success(ticketService.list(qw));//返回到List<Ticket>
    }
}
