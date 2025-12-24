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

//新建ticket
@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    @Autowired
    private WorkOrderService workOrderService;
    //作用是为了下面requireWorkOrderBelongsToCompany这个方法里校验这个工作记录的归属

    @Override
    public Long createTicket(Long companyId, TicketCreateReq req) {
        if (companyId == null) throw new IllegalArgumentException("companyId 不能为空");
        if (req == null) throw new IllegalArgumentException("请求体不能为空");
        if (req.getWorkOrderId() == null) throw new IllegalArgumentException("workOrderId 不能为空");
        if (!StringUtils.hasText(req.getDescription())) throw new IllegalArgumentException("description 不能为空");

        // 关键：先做工单存在性 + 归属校验
        requireWorkOrderBelongsToCompany(companyId, req.getWorkOrderId());

        Ticket t = new Ticket();
        t.setWorkOrderId(req.getWorkOrderId());
        t.setDescription(req.getDescription().trim());
        t.setCreateTime(LocalDateTime.now());

        this.save(t);
        return t.getId();
    }

    @Override
    public Page<Ticket> pageByWorkOrder(Long companyId, Long workOrderId, long page, long size) {
        if (companyId == null) throw new IllegalArgumentException("companyId 不能为空");
        if (workOrderId == null) throw new IllegalArgumentException("workOrderId 不能为空");

        // 关键：先做工单存在性 + 归属校验
        requireWorkOrderBelongsToCompany(companyId, workOrderId);

        Page<Ticket> p = new Page<>(page, size);
        LambdaQueryWrapper<Ticket> qw = new LambdaQueryWrapper<>();
        qw.eq(Ticket::getWorkOrderId, workOrderId)
                .orderByDesc(Ticket::getCreateTime)
                .orderByDesc(Ticket::getId);

        return this.page(p, qw);
    }

    //ticket的私有校验方法
    private WorkOrder requireWorkOrderBelongsToCompany(Long companyId, Long workOrderId) {
        WorkOrder wo = workOrderService.getById(workOrderId);
        if (wo == null) {
            throw new BizException("工单不存在");
        }
        if (wo.getCompanyId() == null || !wo.getCompanyId().equals(companyId)) {
            throw new BizException("无权限访问该工单");
        }
        return wo;
    }

}
