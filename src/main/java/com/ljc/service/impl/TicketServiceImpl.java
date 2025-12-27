package com.ljc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.common.BizException;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.mapper.TicketMapper;
import com.ljc.service.TicketService;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * TicketServiceImpl：Ticket（记录）业务实现
 *
 * ✅ Ticket 的定位（和 WorkOrder 的关系）
 * - WorkOrder：工单主体（可更新内容、可更新状态）
 * - Ticket：工单的“操作记录/沟通记录/历史记录”（append-only，只新增，不修改）
 *
 * ✅ 本类负责什么
 * 1) 新建 ticket（写一条记录）
 * 2) 按工单分页查 ticket（时间线列表）
 * 3) ticket 详情（可选）
 *
 * ✅ 核心安全规则（单租户/多租户都适用）
 * - 所有 Ticket 的读/写，都必须先通过 WorkOrder 做 company 归属校验
 * - 防止“跨公司看/写别人的工单记录”
 */
@Service
public class TicketServiceImpl
        extends ServiceImpl<TicketMapper, Ticket>
        implements TicketService {

    /**
     * 依赖 WorkOrderService：
     * 用于复用 getByIdWithCompany(companyId, workOrderId) 归属校验
     */
    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 创建 Ticket（写一条工单记录）
     *
     * 调用链：
     * Controller -> TicketService.createTicket -> 本方法
     *
     * @param companyId 当前公司
     * @param req       DTO：前端传入的创建参数
     * @return 新增记录 id
     */
    @Override
    public Long createTicket(Long companyId, TicketCreateReq req) {
        // 1) 参数校验（防脏数据）
        if (companyId == null) throw new BizException("companyId 不能为空");
        if (req == null) throw new BizException("请求体不能为空");
        if (req.getWorkOrderId() == null) throw new BizException("workOrderId 不能为空");
        if (!StringUtils.hasText(req.getDescription())) throw new BizException("description 不能为空");

        // 2) 归属校验：这个 workOrderId 是否存在 + 是否属于当前 company
        //    只要这里校验过，下面写 ticket 就不会跨公司乱写
        workOrderService.getByIdWithCompany(companyId, req.getWorkOrderId());

        // 3) 组装实体（append-only）
        Ticket ticket = new Ticket();
        ticket.setWorkOrderId(req.getWorkOrderId());
        ticket.setDescription(req.getDescription().trim());
        ticket.setCreateTime(LocalDateTime.now());

        // 4) 写库并返回 id
        this.save(ticket);
        return ticket.getId();
    }

    /**
     * 按工单分页查询 Ticket（时间线列表）
     *
     * 使用场景：
     * - 工单详情页右侧 “操作记录”
     * - 按时间倒序展示
     *
     * 调用链：
     * Controller -> TicketService.pageByWorkOrder -> 本方法
     */
    @Override
    public Page<Ticket> pageByWorkOrder(Long companyId, Long workOrderId, long page, long size) {
        // 1) 参数校验
        if (companyId == null) throw new BizException("companyId 不能为空");
        if (workOrderId == null) throw new BizException("workOrderId 不能为空");

        // 2) 归属校验：没通过就直接抛异常，Controller 不会继续执行
        workOrderService.getByIdWithCompany(companyId, workOrderId);

        // 3) 分页查询：只查这个工单的 tickets，按时间倒序
        Page<Ticket> p = new Page<>(page, size);
        LambdaQueryWrapper<Ticket> qw = new LambdaQueryWrapper<>();
        qw.eq(Ticket::getWorkOrderId, workOrderId)
                .orderByDesc(Ticket::getCreateTime)
                .orderByDesc(Ticket::getId);

        return this.page(p, qw);
    }

    /**
     * Ticket 详情（可选）
     *
     * 校验闭环：
     * ticketId -> 查 ticket -> 拿到 workOrderId -> 用 WorkOrderService 校验 company 归属
     *
     * 调用链：
     * Controller -> TicketService.getByIdWithCompany -> 本方法
     */
    @Override
    public Ticket getByIdWithCompany(Long companyId, Long ticketId) {
        // 1) 参数校验
        if (companyId == null) throw new BizException("companyId 不能为空");
        if (ticketId == null) throw new BizException("ticketId 不能为空");

        // 2) 查 ticket 本身
        Ticket ticket = this.getById(ticketId);
        if (ticket == null) throw new BizException("记录不存在");

        // 3) 归属校验：通过 ticket 关联的工单来校验 company
        Long workOrderId = ticket.getWorkOrderId();
        if (workOrderId == null) throw new BizException("记录未关联工单");

        // 复用工单归属校验（不通过就抛 BizException）
        workOrderService.getByIdWithCompany(companyId, workOrderId);

        return ticket;
    }
}
