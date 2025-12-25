package com.ljc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.entity.WorkOrder;

public interface WorkOrderService extends IService<WorkOrder> {

    // ✅ 创建工单（带 companyId）
    Long createWorkOrder(Long companyId, WorkOrderCreateReq req);

    // ✅ 更新状态（带 companyId，强制归属校验）
    boolean updateStatusWithCompany(Long companyId, Long workOrderId, String status);

    // ✅ 工单详情（带 companyId）
    WorkOrder getByIdWithCompany(Long companyId, Long workOrderId);

    // ✅ 更新内容（带 companyId）
    boolean updateContentWithCompany(Long companyId, Long workOrderId, String title, String content);

    // ✅ 分页 + 筛选（带 companyId）
    Page<WorkOrder> pageWithCompany(
            Long companyId,
            long page,
            long size,
            String status,
            String keyword,
            Long creatorId,
            Long handlerId
    );
}
