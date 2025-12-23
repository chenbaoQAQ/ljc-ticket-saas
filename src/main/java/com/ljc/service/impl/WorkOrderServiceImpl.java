package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.entity.WorkOrder;
import com.ljc.mapper.WorkOrderMapper;
import com.ljc.service.WorkOrderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
}
