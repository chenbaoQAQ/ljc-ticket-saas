package com.ljc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.common.BizException;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.entity.WorkOrder;
import com.ljc.mapper.TicketMapper;
import com.ljc.service.TicketService;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * Ticket 业务实现类
 *
 * 职责说明：
 * 1. Ticket 是“工单的历史记录”，设计为 append-only（只新增、不修改）
 * 2. 所有 Ticket 操作都必须通过 WorkOrder 做 company 归属校验
 * 3. 不允许跨公司读取或创建记录
 */
@Service
public class TicketServiceImpl
        extends ServiceImpl<TicketMapper, Ticket>
        implements TicketService {

    /**
     * 依赖工单服务：
     * 用于做工单存在性 + company 归属校验
     */
    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 创建一条 Ticket（工单记录）
     *
     * @param companyId 当前公司（单租户阶段固定为 1L）
     * @param req       创建请求
     * @return 新建记录的 id
     */
    @Override
    public Long createTicket(Long companyId, TicketCreateReq req) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (req == null) {
            throw new BizException("请求体不能为空");
        }
        if (req.getWorkOrderId() == null) {
            throw new BizException("workOrderId 不能为空");
        }
        if (!StringUtils.hasText(req.getDescription())) {
            throw new BizException("description 不能为空");
        }

        // 关键：校验工单是否存在，且是否属于当前 company
        workOrderService.getByIdWithCompany(companyId, req.getWorkOrderId());

        // 构造 Ticket 实体（append-only）
        Ticket ticket = new Ticket();
        ticket.setWorkOrderId(req.getWorkOrderId());
        ticket.setDescription(req.getDescription().trim());
        ticket.setCreateTime(LocalDateTime.now());

        this.save(ticket);
        return ticket.getId();
    }

    /**
     * 按工单分页查询 Ticket（时间线）
     *
     * 使用场景：
     * - 工单详情页右侧记录列表
     * - 处理历史时间线
     */
    @Override
    public Page<Ticket> pageByWorkOrder(
            Long companyId,
            Long workOrderId,
            long page,
            long size
    ) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (workOrderId == null) {
            throw new BizException("workOrderId 不能为空");
        }

        // 关键：先校验工单归属，防止跨公司查看记录
        workOrderService.getByIdWithCompany(companyId, workOrderId);

        Page<Ticket> p = new Page<>(page, size);
        LambdaQueryWrapper<Ticket> qw = new LambdaQueryWrapper<>();

        qw.eq(Ticket::getWorkOrderId, workOrderId)
                .orderByDesc(Ticket::getCreateTime)
                .orderByDesc(Ticket::getId);

        return this.page(p, qw);
    }

    /**
     * Ticket 详情查询（可选功能）
     *
     * 校验逻辑：
     * ticket → workOrder → company
     *
     * @param companyId 当前公司
     * @param ticketId  记录 id
     * @return Ticket 详情
     */
    @Override
    public Ticket getByIdWithCompany(Long companyId, Long ticketId) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (ticketId == null) {
            throw new BizException("ticketId 不能为空");
        }

        // 1. 查询 Ticket 本身
        Ticket ticket = this.getById(ticketId);
        if (ticket == null) {
            throw new BizException("记录不存在");
        }

        // 2. 通过 Ticket 关联的工单做 company 归属校验
        Long workOrderId = ticket.getWorkOrderId();
        if (workOrderId == null) {
            throw new BizException("记录未关联工单");
        }

        // 复用工单的 company 校验逻辑
        workOrderService.getByIdWithCompany(companyId, workOrderId);

        return ticket;
    }
}
