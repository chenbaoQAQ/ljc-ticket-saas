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

    // 你原来的全量接口：保留不动
    @GetMapping({"/workorder", "/api/work-orders"})
    public Result<List<WorkOrder>> listWorkOrders() {
        return Result.success(workOrderService.list());
    }

    // ✅ 分页 + 筛选 + 排序（只保留一个，不要重复写两个同名）
    @GetMapping("/api/work-orders/page")
    public Result<Page<WorkOrder>> pageWorkOrders(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long handlerId
    ) {
        Page<WorkOrder> p = new Page<>(page, size);
        LambdaQueryWrapper<WorkOrder> qw = new LambdaQueryWrapper<>();

        // 单租户阶段：只查 companyId=1
        qw.eq(WorkOrder::getCompanyId, 1L);

        if (StringUtils.hasText(status)) {
            qw.eq(WorkOrder::getStatus, status);
        }
        if (creatorId != null) {
            qw.eq(WorkOrder::getCreatorId, creatorId);
        }
        if (handlerId != null) {
            qw.eq(WorkOrder::getHandlerId, handlerId);
        }
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(WorkOrder::getTitle, keyword)
                    .or()
                    .like(WorkOrder::getContent, keyword));
        }

        qw.orderByDesc(WorkOrder::getId);
        return Result.success(workOrderService.page(p, qw));
    }

    // ✅ 创建工单（返回 id）
    @PostMapping("/api/work-orders")
    public Result<Long> createWorkOrder(@RequestBody WorkOrderCreateReq req) {
        Long id = workOrderService.createWorkOrder(req);
        return Result.success(id);
    }

    // ✅ 详情
    @GetMapping("/api/work-orders/{id}")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        WorkOrder wo = workOrderService.getById(id);
        if (wo == null) {
            return Result.error(404, "工单不存在");
        }
        return Result.success(wo);
    }

    // ✅ 改状态（返回更新后的工单）
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

