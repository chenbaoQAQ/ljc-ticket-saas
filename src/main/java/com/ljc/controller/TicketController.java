package com.ljc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.common.context.AuthContext;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import com.ljc.util.TimeUtil;
import com.ljc.vo.TicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping("/api/tickets")
    public Result<Long> createTicket(@RequestBody TicketCreateReq req) {
        Long companyId = AuthContext.getCompanyId();
        Long id = ticketService.createTicket(companyId, req);
        return Result.success(id);
    }

    @GetMapping("/api/tickets")
    public Result<Page<TicketVO>> pageByWorkOrder(
            @RequestParam Long workOrderId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Long companyId = AuthContext.getCompanyId();

        Page<Ticket> entityPage =
                ticketService.pageByWorkOrder(companyId, workOrderId, page, size);

        return Result.success(toVOPage(entityPage));
    }

    @GetMapping("/api/tickets/{id}")
    public Result<TicketVO> getTicketDetail(@PathVariable Long id) {
        Long companyId = AuthContext.getCompanyId();
        Ticket ticket = ticketService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(ticket));
    }

    // VO 转换区不动
    private TicketVO toVO(Ticket t) {
        if (t == null) return null;

        TicketVO vo = new TicketVO();
        vo.setId(t.getId());
        vo.setWorkOrderId(t.getWorkOrderId());
        vo.setDescription(t.getDescription());
        vo.setCreateTime(TimeUtil.format(t.getCreateTime()));
        vo.setUpdateTime(TimeUtil.format(t.getUpdateTime()));

        return vo;
    }

    private Page<TicketVO> toVOPage(Page<Ticket> entityPage) {
        Page<TicketVO> voPage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal()
        );

        voPage.setRecords(
                entityPage.getRecords().stream()
                        .map(this::toVO)
                        .collect(Collectors.toList())
        );

        return voPage;
    }
}
