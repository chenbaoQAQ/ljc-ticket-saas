package com.ljc.controller;

import com.ljc.common.Result;
import com.ljc.entity.WorkOrder;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping({"/workorder", "/api/work-orders"})
    public Result<List<WorkOrder>> listWorkOrders() {
        return Result.success(workOrderService.list());
    }
}
