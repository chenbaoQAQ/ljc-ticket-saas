package com.ljc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.common.BizException;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.entity.WorkOrder;
import com.ljc.mapper.WorkOrderMapper;
import com.ljc.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * WorkOrder 业务实现
 *
 * 统一原则：
 * - companyId 由 Controller（或登录上下文）传入
 * - Service 负责：校验 + company 归属 + 写库/查库
 */
@Service
public class WorkOrderServiceImpl
        extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {

    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "IN_PROGRESS", "CLOSED");

    /**
     * 创建工单
     * 1) 校验请求
     * 2) 组装 WorkOrder 实体
     * 3) 写入数据库，返回 id
     */
    @Override
    public Long createWorkOrder(Long companyId, WorkOrderCreateReq req) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (req == null) {
            throw new BizException("请求体不能为空");
        }
        if (!StringUtils.hasText(req.getTitle())) {
            throw new BizException("title 不能为空");
        }
        if (!StringUtils.hasText(req.getContent())) {
            throw new BizException("content 不能为空");
        }
        if (req.getCreatorId() == null) {
            throw new BizException("creatorId 不能为空");
        }

        WorkOrder wo = new WorkOrder();
        wo.setCompanyId(companyId);
        wo.setTitle(req.getTitle().trim());
        wo.setContent(req.getContent().trim());
        wo.setCreatorId(req.getCreatorId());

        wo.setHandlerId(null);
        wo.setStatus("OPEN");

        this.save(wo);
        return wo.getId();
    }

    /**
     * 工单详情（带 company 归属校验）
     * - 用于详情接口
     * - Ticket 创建/查询前也会复用它做归属校验
     */
    @Override
    public WorkOrder getByIdWithCompany(Long companyId, Long workOrderId) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (workOrderId == null) {
            throw new BizException("工单 id 不能为空");
        }

        WorkOrder wo = this.getById(workOrderId);
        if (wo == null) {
            throw new BizException("工单不存在");
        }
        if (wo.getCompanyId() == null || !wo.getCompanyId().equals(companyId)) {
            throw new BizException("无权限访问该工单");
        }
        return wo;
    }

    /**
     * 更新工单内容（带 company 校验）
     * - 至少更新 title/content 其中一个
     */
    @Override
    public boolean updateContentWithCompany(Long companyId, Long workOrderId, String title, String content) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (workOrderId == null) {
            throw new BizException("工单 id 不能为空");
        }

        boolean hasTitle = StringUtils.hasText(title);
        boolean hasContent = StringUtils.hasText(content);
        if (!hasTitle && !hasContent) {
            throw new BizException("更新内容不能为空");
        }

        WorkOrder wo = this.getByIdWithCompany(companyId, workOrderId);

        if (hasTitle) {
            wo.setTitle(title.trim());
        }
        if (hasContent) {
            wo.setContent(content.trim());
        }

        return this.updateById(wo);
    }

    /**
     * 更新工单状态（带 company 校验）
     */
    @Override
    public boolean updateStatusWithCompany(Long companyId, Long workOrderId, String status) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (workOrderId == null) {
            throw new BizException("工单 id 不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new BizException("status 不能为空");
        }

        String s = status.trim();
        if (!ALLOWED_STATUS.contains(s)) {
            throw new BizException("非法状态: " + s);
        }

        WorkOrder wo = this.getByIdWithCompany(companyId, workOrderId);
        wo.setStatus(s);
        return this.updateById(wo);
    }

    /**
     * 分页 + 筛选（带 company 条件）
     */
    @Override
    public Page<WorkOrder> pageWithCompany(
            Long companyId,
            long page,
            long size,
            String status,
            String keyword,
            Long creatorId,
            Long handlerId
    ) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }

        Page<WorkOrder> p = new Page<>(page, size);
        LambdaQueryWrapper<WorkOrder> qw = new LambdaQueryWrapper<>();

        qw.eq(WorkOrder::getCompanyId, companyId);

        if (StringUtils.hasText(status)) {
            qw.eq(WorkOrder::getStatus, status.trim());
        }
        if (creatorId != null) {
            qw.eq(WorkOrder::getCreatorId, creatorId);
        }
        if (handlerId != null) {
            qw.eq(WorkOrder::getHandlerId, handlerId);
        }
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            qw.and(w -> w.like(WorkOrder::getTitle, kw).or().like(WorkOrder::getContent, kw));
        }

        qw.orderByDesc(WorkOrder::getId);
        return this.page(p, qw);
    }
}
