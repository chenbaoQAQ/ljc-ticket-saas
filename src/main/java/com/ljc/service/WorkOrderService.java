package com.ljc.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.entity.WorkOrder;

public interface WorkOrderService extends IService<WorkOrder> {
    Long createWorkOrder(WorkOrderCreateReq req);
    boolean updateStatus(Long id, String status);
    boolean updateContentWithCompany(Long companyId, Long workOrderId, String title, String content);


    WorkOrder getByIdWithCompany(Long companyId, Long workOrderId);

    //在 Service 层“声明一种能力”：
    //按 company 维度，分页查询工单（带筛选条件）
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


