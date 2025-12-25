package com.ljc.controller;

import com.ljc.dto.WorkOrderUpdateReq;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.dto.WorkOrderStatusReq;
import com.ljc.entity.WorkOrder;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // 分页 + 筛选 + 排序
    @GetMapping("/api/work-orders")
    public Result<Page<WorkOrder>> pageWorkOrders(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long handlerId
    ) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        Page<WorkOrder> result = workOrderService.pageWithCompany(
                companyId,
                page,
                size,
                status,
                keyword,
                creatorId,
                handlerId
        );
        return Result.success(result);
    }


    //  创建工单（返回 id）
    @PostMapping("/api/work-orders")
    public Result<Long> createWorkOrder(@RequestBody WorkOrderCreateReq req) {
        Long id = workOrderService.createWorkOrder(req);
        return Result.success(id);
    }

    //  详情
    @GetMapping("/api/work-orders/{id}")
    public Result<WorkOrder> getWorkOrderDetail(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从登录上下文获取

        WorkOrder workOrder = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(workOrder);
    }


    //更新内容
    @PutMapping("/api/work-orders/{id}")
    public Result<Void> updateContent(
            @PathVariable Long id,
            @RequestBody WorkOrderUpdateReq req
    ) {
        Long companyId = 1L; // TODO: 登录后从上下文取

        workOrderService.updateContentWithCompany(
                companyId,
                id,
                req.getTitle(),
                req.getContent()
        );

        return Result.success(null);
    }



    //  改状态（返回更新后的工单）
    @PutMapping("/api/work-orders/{id}/status")
    public Result<WorkOrder> updateStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusReq req
    ) {
        workOrderService.updateStatus(id, req.getStatus());
        return Result.success(workOrderService.getById(id));
    }




}

