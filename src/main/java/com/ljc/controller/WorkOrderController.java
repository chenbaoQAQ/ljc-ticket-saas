package com.ljc.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljc.common.Result;
import com.ljc.dto.WorkOrderCreateReq;
import com.ljc.dto.WorkOrderStatusReq;
import com.ljc.dto.WorkOrderUpdateReq;
import com.ljc.entity.WorkOrder;
import com.ljc.service.WorkOrderService;
import com.ljc.vo.WorkOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 分页 + 筛选 + 排序（返回 VO）
     *
     * GET /api/work-orders?page=1&size=10&status=OPEN&keyword=xx&creatorId=1&handlerId=2
     */
    @GetMapping("/api/work-orders")
    public Result<Page<WorkOrderVO>> pageWorkOrders(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long handlerId
    ) {
        Long companyId = 1L; // TODO: 登录后从上下文取

        Page<WorkOrder> entityPage = workOrderService.pageWithCompany(
                companyId,
                page,
                size,
                status,
                keyword,
                creatorId,
                handlerId
        );

        // Page<WorkOrder> -> Page<WorkOrderVO>
        Page<WorkOrderVO> voPage = toVOPage(entityPage);
        return Result.success(voPage);
    }

    /**
     * 创建工单（返回 id）
     *
     * POST /api/work-orders
     */
    @PostMapping("/api/work-orders")
    public Result<Long> createWorkOrder(@RequestBody WorkOrderCreateReq req) {
        Long id = workOrderService.createWorkOrder(req);
        return Result.success(id);
    }

    /**
     * 工单详情（返回 VO）
     *
     * GET /api/work-orders/{id}
     */
    @GetMapping("/api/work-orders/{id}")
    public Result<WorkOrderVO> getWorkOrderDetail(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        WorkOrder wo = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(wo));
    }

    /**
     * 更新工单内容（返回 void）
     *
     * PUT /api/work-orders/{id}
     */
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

    /**
     * 更新工单状态（返回 VO）
     *
     * PUT /api/work-orders/{id}/status
     */
    @PutMapping("/api/work-orders/{id}/status")
    public Result<WorkOrderVO> updateStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusReq req
    ) {
        // 这里先保持你原来的 updateStatus 逻辑（它目前没做 company 校验）
        workOrderService.updateStatus(id, req.getStatus());

        Long companyId = 1L; // TODO: 登录后从上下文取
        WorkOrder wo = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(wo));
    }

    // =========================
    // VO 转换区（只做字段筛选）
    // =========================

    private WorkOrderVO toVO(WorkOrder wo) {
        if (wo == null) return null;

        WorkOrderVO vo = new WorkOrderVO();
        vo.setId(wo.getId());
        vo.setTitle(wo.getTitle());
        vo.setContent(wo.getContent());
        vo.setStatus(wo.getStatus());
        vo.setCreatorId(wo.getCreatorId());
        vo.setHandlerId(wo.getHandlerId());


        return vo;
    }

    private Page<WorkOrderVO> toVOPage(Page<WorkOrder> entityPage) {
        Page<WorkOrderVO> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());
        voPage.setPages(entityPage.getPages());

        List<WorkOrderVO> voRecords = entityPage.getRecords()
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        voPage.setRecords(voRecords);
        return voPage;
    }
}
