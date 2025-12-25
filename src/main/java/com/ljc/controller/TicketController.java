package com.ljc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.service.TicketService;
import com.ljc.vo.TicketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * TicketController（接口层）
 *
 * 这份 Controller 的职责和 WorkOrderController 一样：
 * 1) 接收 HTTP 请求参数 / Body
 * 2) 调用 TicketService 完成业务逻辑（校验 + 查库 + 新增）
 * 3) 把 Entity 转成 VO（只把前端要的字段返回）
 * 4) 用 Result 统一返回结构
 *
 * Ticket 的特点：
 * - Ticket（记录）是“挂在某个 WorkOrder（工单）下面”的
 * - 所以很多操作都要先校验：workOrder 是否存在 + 是否属于当前 company
 * - 这个归属校验是在 Service 层做（requireWorkOrderBelongsToCompany / getByIdWithCompany）
 */
@RestController
@RequestMapping
public class TicketController {

    /**
     * Spring 注入 TicketService 的实现类（TicketServiceImpl）
     * Controller 依赖接口，不直接依赖实现类。
     */
    @Autowired
    private TicketService ticketService;

    /**
     * 1) 创建 Ticket（返回 id）
     *
     * HTTP: POST /api/tickets
     * Body(JSON): TicketCreateReq
     *
     * Controller 做的事：
     * A. @RequestBody 把前端 JSON 解析成 TicketCreateReq（输入 DTO）
     * B. companyId 目前写死 1L（后面登录了从上下文取）
     * C. 调用 ticketService.createTicket(companyId, req)
     *    - Service 会做：参数校验 / 工单存在性校验 / 工单归属 company 校验 / save 入库
     * D. Service 返回新 Ticket 的 id
     * E. Result.success(id) 返回
     */
    @PostMapping("/api/tickets")
    public Result<Long> createTicket(@RequestBody TicketCreateReq req) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        Long id = ticketService.createTicket(companyId, req);
        return Result.success(id);
    }

    /**
     * 2) 按工单分页查询 Ticket（返回 VO）
     *
     * HTTP: GET /api/tickets?workOrderId=1&page=1&size=10
     *
     * Controller 做的事：
     * A. 从 Query 参数拿 workOrderId/page/size
     * B. companyId 目前写死 1L
     * C. 调用 ticketService.pageByWorkOrder(companyId, workOrderId, page, size)
     *    - Service 会先校验：该工单存在且属于 company
     *    - 然后按 workOrderId 查 Ticket，按 createTime/id 倒序，做分页
     * D. Service 返回 Page<Ticket>（实体页）
     * E. Controller 把实体页转成 Page<TicketVO>
     * F. 返回 Result.success(voPage)
     *
     * 说明：
     * - 这个接口返回的是“列表页”（records 是多条）
     * - 前端用来展示：某个工单下面的所有记录（分页）
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
     * 3) Ticket 详情（返回 VO）
     *
     * HTTP: GET /api/tickets/{id}
     *
     * Controller 做的事：
     * A. 从路径取 ticketId
     * B. companyId 目前写死 1L
     * C. 调用 ticketService.getByIdWithCompany(companyId, id)
     *    - Service 会：先查 ticket 是否存在
     *    - 再用 ticket.workOrderId 找到对应工单，校验工单是否属于 company
     *    - 校验通过才返回 ticket
     * D. Controller ticket -> VO
     * E. Result.success(vo) 返回
     *
     * 说明：
     * - 这个接口返回的是“单条详情”
     * - 和分页接口区别：分页返回多条 records，详情只返回 1 条
     */
    @GetMapping("/api/tickets/{id}")
    public Result<TicketVO> getTicketDetail(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        Ticket ticket = ticketService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(ticket));
    }

    // =========================
    // VO 转换区（只控制输出字段）
    // =========================

    /**
     * Entity -> VO
     * 目的：不把数据库实体完整暴露给前端，只返回需要的字段
     */
    private TicketVO toVO(Ticket t) {
        if (t == null) return null;

        TicketVO vo = new TicketVO();
        vo.setId(t.getId());
        vo.setWorkOrderId(t.getWorkOrderId());
        vo.setDescription(t.getDescription());

        // createTime 先不统一处理，等你整体时间策略确定
        // vo.setCreateTime(TimeUtil.format(t.getCreateTime()));

        return vo;
    }

    /**
     * Page<Entity> -> Page<VO>
     * 复制分页信息（current/size/total）+ records 转 VO
     */
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
