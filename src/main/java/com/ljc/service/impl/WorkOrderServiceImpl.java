package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.entity.WorkOrder;
import com.ljc.mapper.WorkOrderMapper;
import com.ljc.service.WorkOrderService;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderServiceImpl
        extends ServiceImpl<WorkOrderMapper, WorkOrder>
        implements WorkOrderService {
}
