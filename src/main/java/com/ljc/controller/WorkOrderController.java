package com.ljc.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.dto.WorkOrderStatusReq;
import com.ljc.entity.WorkOrder;
import com.ljc.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    // ✅ 分页 + 筛选 + 排序
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
    public Result<WorkOrder> getById(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        WorkOrder wo = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(wo);
    }


    //  改状态（返回更新后的工单）
    @PutMapping("/api/work-orders/{id}/status")
    public Result<WorkOrder> updateStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusReq req
    ) {
        try {
            boolean ok = workOrderService.updateStatus(id, req.getStatus());
            if (!ok) {
                return Result.error(500, "更新失败");
            }
            return Result.success(workOrderService.getById(id));
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }



}

