package com.ljc.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/*
qw.eq(...)      // 等于
qw.like(...)    // 模糊查询
qw.orderByDesc // 排序
.orderByDesc等于sql里面的降序
.orderByAsc等于sql里面的升序
 */

@RestController
@RequestMapping
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/api/tickets")
    public Result<Long> createTicket(@RequestBody TicketCreateReq req) {
        Long companyId = 1L; // TODO: 登录后从用户上下文取
        Long id = ticketService.createTicket(companyId, req);
        return Result.success(id);
    }


    //做分页功能+排序（可以直接找第x页的x条数据）
    @GetMapping("/api/tickets")
    public Result<Page<Ticket>> listByWorkOrder(
            @RequestParam Long workOrderId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Long companyId = 1L; // TODO: 登录后从用户上下文取
        Page<Ticket> result = ticketService.pageByWorkOrder(companyId, workOrderId, page, size);
        return Result.success(result);
    }



}

