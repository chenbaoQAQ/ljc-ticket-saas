package com.ljc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.entity.WorkOrder;

public interface WorkOrderService extends IService<WorkOrder> {
    Long createWorkOrder(WorkOrderCreateReq req);
    boolean updateStatus(Long id, String status);
}


