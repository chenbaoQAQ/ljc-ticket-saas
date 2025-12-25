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
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Service
public class WorkOrderServiceImpl
        extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {

    //允许的状态集合
    private static final Set<String> ALLOWED_STATUS = Set.of("OPEN", "IN_PROGRESS", "CLOSED");

    //输入校验，防止脏数据
    @Override
    public Long createWorkOrder(WorkOrderCreateReq req) {
        if (req == null) {
            throw new IllegalArgumentException("请求体不能为空");
        }
        if (!StringUtils.hasText(req.getTitle())) {
            throw new IllegalArgumentException("title 不能为空");
        }
        if (!StringUtils.hasText(req.getContent())) {
            throw new IllegalArgumentException("content 不能为空");
        }
        if (req.getCreatorId() == null) {
            throw new IllegalArgumentException("creatorId 不能为空");
        }
        //新建一个
        WorkOrder wo = new WorkOrder();
        wo.setTitle(req.getTitle().trim());
        wo.setContent(req.getContent().trim());
        wo.setCreatorId(req.getCreatorId());
        //组装实体（处理人是空的，公司是1L，工单状态是OPEN）
        wo.setHandlerId(null);
        wo.setCompanyId(1L);
        wo.setStatus("OPEN");
        //写入数据库
        this.save(wo);
        return wo.getId();
    }

    //更新工单
    @Override
    public boolean updateStatus(Long id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("id 不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new IllegalArgumentException("status 不能为空");
        }
        String s = status.trim();
        if (!ALLOWED_STATUS.contains(s)) {
            throw new IllegalArgumentException("非法状态: " + s);
        }

        //用id查这个工单，如果有存在wo
        WorkOrder wo = this.getById(id);
        if (wo == null) {
            throw new IllegalArgumentException("工单不存在");
        }

        //改成新的值，写回数据库
        wo.setStatus(s);
        return this.updateById(wo);
    }

    //工单的归属感校验
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

    //公司的归属感校验
    @Override
    public boolean updateContentWithCompany(Long companyId, Long workOrderId, String title, String content) {
        if (companyId == null) {
            throw new BizException("companyId 不能为空");
        }
        if (workOrderId == null) {
            throw new BizException("工单 id 不能为空");
        }

        // 至少要更新一个字段
        boolean hasTitle = StringUtils.hasText(title);
        boolean hasContent = StringUtils.hasText(content);
        if (!hasTitle && !hasContent) {
            throw new BizException("更新内容不能为空");
        }

        // 先做存在性 + 归属校验（复用你已有的方法）
        WorkOrder wo = this.getByIdWithCompany(companyId, workOrderId);

        // 只更新允许的字段
        if (hasTitle) {
            wo.setTitle(title.trim());
        }
        if (hasContent) {
            wo.setContent(content.trim());
        }

        // 写回数据库
        return this.updateById(wo);
    }

    //分页 + 条件筛选的工单查询
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

        // 单租户阶段：只查当前 company
        qw.eq(WorkOrder::getCompanyId, companyId);

        if (StringUtils.hasText(status)) {//status：不为空
            qw.eq(WorkOrder::getStatus, status);
        }
        if (creatorId != null) { //creatorId：按创建人筛选
            qw.eq(WorkOrder::getCreatorId, creatorId);
        }
        if (handlerId != null) {//handlerId：按处理人筛选
            qw.eq(WorkOrder::getHandlerId, handlerId);
        }
        if (StringUtils.hasText(keyword)) {//keyword：搜索关键词
            qw.and(w -> w
                    .like(WorkOrder::getTitle, keyword)
                    .or()
                    .like(WorkOrder::getContent, keyword)
            );
        }

        qw.orderByDesc(WorkOrder::getId);
        return this.page(p, qw);
    }

}
