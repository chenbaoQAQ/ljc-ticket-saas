package com.ljc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import com.ljc.vo.TicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ljc.util.TimeUtil;

import java.util.stream.Collectors;

@RestController
@RequestMapping
public class TicketController {

    @Autowired
    private TicketService ticketService;

    /**
     * 创建 Ticket（返回 id）
     *
     * POST /api/tickets
     */
    @PostMapping("/api/tickets")
    public Result<Long> createTicket(@RequestBody TicketCreateReq req) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        Long id = ticketService.createTicket(companyId, req);
        return Result.success(id);
    }

    /**
     * 按工单分页查询 Ticket（返回 VO）
     *
     * GET /api/tickets?workOrderId=1&page=1&size=10
     */
    @GetMapping("/api/tickets")
    public Result<Page<TicketVO>> pageByWorkOrder(
            @RequestParam Long workOrderId,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        Long companyId = 1L; // TODO: 登录后从上下文取

        Page<Ticket> entityPage =
                ticketService.pageByWorkOrder(companyId, workOrderId, page, size);

        return Result.success(toVOPage(entityPage));
    }

    /**
     * Ticket 详情（返回 VO）
     *
     * GET /api/tickets/{id}
     */
    @GetMapping("/api/tickets/{id}")
    public Result<TicketVO> getTicketDetail(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        Ticket ticket = ticketService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(ticket));
    }

    // =========================
    // VO 转换区
    // =========================

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
