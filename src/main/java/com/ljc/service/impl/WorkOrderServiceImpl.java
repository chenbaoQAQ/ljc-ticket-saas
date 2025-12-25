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
 * WorkOrderServiceImpl（业务层实现）
 *
 * 职责：
 * 1) 做业务校验（参数合法性/状态合法性/归属校验）
 * 2) 调用 MyBatis-Plus 的通用 CRUD（save/getById/updateById/page）
 *
 * 注意：
 * - 这里不写 Controller 注解，不处理 HTTP 参数
 * - 这里抛 BizException / IllegalArgumentException 交给 GlobalExceptionHandler 统一返回
 */
@Service
public class WorkOrderServiceImpl
        extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {

    /** 允许的工单状态 */
    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "IN_PROGRESS", "CLOSED");

    /**
     * 创建工单
     *
     * 做的事：
     * 1) 校验 req 必填字段
     * 2) 组装 WorkOrder 实体（默认 status=OPEN，handlerId=null）
     * 3) save 入库
     * 4) 返回新工单 id
     *
     * 注意：这里 companyId 目前写死 1L（后面建议改成从 Controller 传入）
     */
    @Override
    public Long createWorkOrder(WorkOrderCreateReq req) {
        if (req == null) throw new IllegalArgumentException("请求体不能为空");
        if (!StringUtils.hasText(req.getTitle())) throw new IllegalArgumentException("title 不能为空");
        if (!StringUtils.hasText(req.getContent())) throw new IllegalArgumentException("content 不能为空");
        if (req.getCreatorId() == null) throw new IllegalArgumentException("creatorId 不能为空");

        WorkOrder wo = new WorkOrder();
        wo.setTitle(req.getTitle().trim());
        wo.setContent(req.getContent().trim());
        wo.setCreatorId(req.getCreatorId());

        wo.setHandlerId(null);
        wo.setCompanyId(1L); // TODO: 后面改成入参 companyId
        wo.setStatus("OPEN");

        this.save(wo);
        return wo.getId();
    }

    /**
     * 更新工单状态
     *
     * 做的事：
     * 1) 校验 id/status
     * 2) 校验 status 是否属于允许集合
     * 3) 查工单是否存在
     * 4) 更新并写回
     *
     * 注意：目前没做 companyId 归属校验（后面再加）
     */
    @Override
    public boolean updateStatus(Long id, String status) {
        if (id == null) throw new IllegalArgumentException("id 不能为空");
        if (!StringUtils.hasText(status)) throw new IllegalArgumentException("status 不能为空");

        String s = status.trim();
        if (!ALLOWED_STATUS.contains(s)) throw new IllegalArgumentException("非法状态: " + s);

        WorkOrder wo = this.getById(id);
        if (wo == null) throw new IllegalArgumentException("工单不存在");

        wo.setStatus(s);
        return this.updateById(wo);
    }

    /**
     * 工单详情查询 + company 归属校验
     *
     * 做的事：
     * 1) 校验 companyId/workOrderId
     * 2) 查工单是否存在
     * 3) 校验 wo.companyId == companyId
     * 4) 返回工单实体
     *
     * Controller 用它来做“详情接口”
     */
    @Override
    public WorkOrder getByIdWithCompany(Long companyId, Long workOrderId) {
        if (companyId == null) throw new BizException("companyId 不能为空");
        if (workOrderId == null) throw new BizException("工单 id 不能为空");

        WorkOrder wo = this.getById(workOrderId);
        if (wo == null) throw new BizException("工单不存在");

        if (wo.getCompanyId() == null || !wo.getCompanyId().equals(companyId)) {
            throw new BizException("无权限访问该工单");
        }
        return wo;
    }

    /**
     * 更新工单内容 + company 归属校验
     *
     * 为什么叫 updateContentWithCompany？
     * - 因为它在更新前会做“归属校验”
     *
     * 做的事：
     * 1) 校验 companyId/workOrderId
     * 2) title/content 至少传一个（不然没意义）
     * 3) 复用 getByIdWithCompany 做：存在性 + 归属校验
     * 4) 只更新允许字段（title/content）
     * 5) updateById 写回
     */
    @Override
    public boolean updateContentWithCompany(Long companyId, Long workOrderId, String title, String content) {
        if (companyId == null) throw new BizException("companyId 不能为空");
        if (workOrderId == null) throw new BizException("工单 id 不能为空");

        boolean hasTitle = StringUtils.hasText(title);
        boolean hasContent = StringUtils.hasText(content);
        if (!hasTitle && !hasContent) throw new BizException("更新内容不能为空");

        WorkOrder wo = this.getByIdWithCompany(companyId, workOrderId);

        if (hasTitle) wo.setTitle(title.trim());
        if (hasContent) wo.setContent(content.trim());

        return this.updateById(wo);
    }

    /**
     * 分页 + 筛选（只查当前 company 的工单）
     *
     * 这个方法“只负责查数据”
     * - Controller 拿到 Page<WorkOrder> 后再转成 Page<WorkOrderVO>
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
        if (companyId == null) throw new BizException("companyId 不能为空");

        Page<WorkOrder> p = new Page<>(page, size);
        LambdaQueryWrapper<WorkOrder> qw = new LambdaQueryWrapper<>();

        qw.eq(WorkOrder::getCompanyId, companyId);

        if (StringUtils.hasText(status)) qw.eq(WorkOrder::getStatus, status);
        if (creatorId != null) qw.eq(WorkOrder::getCreatorId, creatorId);
        if (handlerId != null) qw.eq(WorkOrder::getHandlerId, handlerId);

        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(WorkOrder::getTitle, keyword)
                    .or()
                    .like(WorkOrder::getContent, keyword));
        }

        qw.orderByDesc(WorkOrder::getId);
        return this.page(p, qw);
    }
}
