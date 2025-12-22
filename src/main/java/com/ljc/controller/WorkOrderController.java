package com.ljc.controller;

import com.ljc.entity.WorkOrder;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @GetMapping("workorder")
    public List<WorkOrder> getAllOrders(){
        return workOrderService.list();
    }
}
