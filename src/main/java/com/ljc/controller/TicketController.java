package com.ljc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.entity.WorkOrder;
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
        Long companyId = 1L; // TODO: 登录后从用户上下文取
        Long id = ticketService.createTicket(companyId, req);
        return Result.success(id);
    }


    //做分页功能+排序（可以直接找第x页的x条数据）
    @GetMapping("/api/tickets/page")
    public Result<Page<Ticket>> pageByWorkOrder(
            @RequestParam Long workOrderId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Long companyId = 1L; // TODO: 登录后从用户上下文取
        Page<Ticket> result = ticketService.pageByWorkOrder(companyId, workOrderId, page, size);
        return Result.success(result);
    }


}
        /*
        qw.eq(...)      // 等于
        qw.like(...)    // 模糊查询
        qw.orderByDesc // 排序
        .orderByDesc等于sql里面的降序
        .orderByAsc等于sql里面的升序
         */
