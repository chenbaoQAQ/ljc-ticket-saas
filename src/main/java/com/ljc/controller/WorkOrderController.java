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

import java.util.stream.Collectors;

/**
 * WorkOrderController（接口层）
 *
 * 你可以把 Controller 当成“前端和 Service 之间的翻译官”：
 * 1) 负责接收 HTTP 请求（路径 / 参数 / body）
 * 2) 把请求参数交给 Service 做业务（校验 / 查库 / 更新）
 * 3) 把 Service 返回的 Entity 转成 VO（只返回给前端想看的字段）
 * 4) 用 Result 统一包一层返回给前端
 *
 * Controller 自己不写业务规则（比如归属校验怎么做），这些都放 ServiceImpl 里。
 */
@RestController
@RequestMapping
public class WorkOrderController {

    /**
     * Spring 注入 WorkOrderService 的实现类（你写的 WorkOrderServiceImpl）
     * 你在 Controller 里只面向接口 WorkOrderService 编程，不关心具体实现类名字。
     */
    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 1) 工单分页 + 筛选（返回 VO）
     *
     * HTTP: GET /api/work-orders?page=1&size=10&status=OPEN&keyword=xx&creatorId=1&handlerId=2
     *
     * 这段 Controller 做的事按顺序就是：
     * A. 从 Query 参数里取 page/size/status/keyword/creatorId/handlerId
     * B. 组一个 companyId（目前写死 1L，后面登录了会从“用户上下文”拿）
     * C. 调用 Service.pageWithCompany(...) 让业务层去“查数据库并做筛选”
     * D. Service 返回的是 Page<WorkOrder>（实体页）
     * E. Controller 把 Page<WorkOrder> 转成 Page<WorkOrderVO>（只改返回结构，不动数据库）
     * F. 用 Result.success(...) 包装后返回给前端
     */
    @GetMapping("/api/work-orders")
    public Result<Page<WorkOrderVO>> pageWorkOrders(
            @RequestParam(defaultValue = "1") long page,          // 当前页码，默认 1
            @RequestParam(defaultValue = "10") long size,         // 每页条数，默认 10
            @RequestParam(required = false) String status,        // 可选筛选：状态
            @RequestParam(required = false) String keyword,       // 可选筛选：关键字（title/content like）
            @RequestParam(required = false) Long creatorId,       // 可选筛选：创建人
            @RequestParam(required = false) Long handlerId        // 可选筛选：处理人
    ) {
        Long companyId = 1L; // TODO: 登录后从上下文取（现在是“单租户模拟”）

        // 交给 Service 干活：查库 + company 归属过滤 + 条件筛选 + 排序 + 分页
        Page<WorkOrder> entityPage = workOrderService.pageWithCompany(
                companyId, page, size, status, keyword, creatorId, handlerId
        );

        // Controller 只负责“返回结构”：把实体页转成 VO 页
        return Result.success(toVOPage(entityPage));
    }

    /**
     * 2) 创建工单（返回 id）
     *
     * HTTP: POST /api/work-orders
     * Body(JSON): WorkOrderCreateReq
     *
     * Controller 做的事：
     * A. @RequestBody 把前端 JSON 反序列化成 WorkOrderCreateReq（输入 DTO）
     * B. 调用 Service.createWorkOrder(req) 去做校验 + save 入库
     * C. Service 返回新工单 id
     * D. 用 Result.success(id) 返回
     *
     * 注意：创建一般直接返回 id 就够了（前端要详情可以再 GET /{id}）
     */
    @PostMapping("/api/work-orders")
    public Result<Long> createWorkOrder(@RequestBody WorkOrderCreateReq req) {
        Long id = workOrderService.createWorkOrder(req);
        return Result.success(id);
    }

    /**
     * 3) 工单详情（返回 VO）
     *
     * HTTP: GET /api/work-orders/{id}
     *
     * Controller 做的事：
     * A. 从路径里拿 id（@PathVariable）
     * B. companyId 目前写死 1L（后面从登录上下文取）
     * C. 调 Service.getByIdWithCompany(companyId, id)
     *    - 这个 Service 方法内部会：查工单是否存在 + 校验 company 归属
     *    - 如果不存在/无权限，会直接 throw BizException
     * D. Service 返回 WorkOrder 实体 wo
     * E. Controller 把 wo -> VO（toVO）
     * F. 返回 Result.success(vo)
     */
    @GetMapping("/api/work-orders/{id}")
    public Result<WorkOrderVO> getWorkOrderDetail(@PathVariable Long id) {
        Long companyId = 1L; // TODO: 登录后从上下文取
        WorkOrder wo = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(wo));
    }

    /**
     * 4) 更新工单内容（返回 void）
     *
     * HTTP: PUT /api/work-orders/{id}
     * Body(JSON): WorkOrderUpdateReq（可能包含 title/content 的更新值）
     *
     * Controller 做的事：
     * A. 路径取 id
     * B. Body 取 req（更新 DTO）
     * C. 调 Service.updateContentWithCompany(companyId, id, title, content)
     *    - Service 内部会：做 company 归属校验 + 至少更新一个字段的校验 + updateById 写回
     * D. 成功就返回 200
     *
     * 为什么返回 void？
     * - 更新成功只告诉前端“成功”即可
     * - 如果你想“顺便把更新后的最新详情返回”，也可以改成返回 WorkOrderVO（你后面再做）
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
     * 5) 更新工单状态（返回 VO）
     *
     * HTTP: PUT /api/work-orders/{id}/status
     * Body(JSON): WorkOrderStatusReq（只有 status）
     *
     * Controller 做的事：
     * A. 路径取 id
     * B. Body 取 status
     * C. 调 Service.updateStatus(id, status) 执行更新（校验 + updateById）
     * D. 为了返回 VO（让前端立刻拿到最新状态），再查一次详情：
     *    Service.getByIdWithCompany(companyId, id)
     * E. wo -> VO
     * F. 返回 Result.success(vo)
     *
     * 你后面也可以把 updateStatus 改成带 companyId 的版本（更安全），现在先这样也能跑通。
     */
    @PutMapping("/api/work-orders/{id}/status")
    public Result<WorkOrderVO> updateStatus(
            @PathVariable Long id,
            @RequestBody WorkOrderStatusReq req
    ) {
        // 更新动作交给 Service
        workOrderService.updateStatus(id, req.getStatus());

        // 为了把“更新后的最新数据”返回给前端，再查一次
        Long companyId = 1L; // TODO: 登录后从上下文取
        WorkOrder wo = workOrderService.getByIdWithCompany(companyId, id);
        return Result.success(toVO(wo));
    }

    // =========================
    // VO 转换（只做返回结构，不碰业务规则）
    // =========================

    /**
     * Entity -> VO
     * 原则：只挑前端要的字段，不把数据库实体完整暴露出去
     */
    private WorkOrderVO toVO(WorkOrder wo) {
        if (wo == null) return null;

        WorkOrderVO vo = new WorkOrderVO();
        vo.setId(wo.getId());
        vo.setTitle(wo.getTitle());
        vo.setContent(wo.getContent());
        vo.setStatus(wo.getStatus());
        vo.setCreatorId(wo.getCreatorId());
        vo.setHandlerId(wo.getHandlerId());

        // 先不处理 createTime：你实体字段还没统一时，先别急着输出
        // vo.setCreateTime(TimeUtil.format(wo.getCreateTime()));

        return vo;
    }

    /**
     * Page<Entity> -> Page<VO>
     *
     * Page 里除了 records，还有 current/size/total 等分页信息
     * 我们要做的是：
     * 1) 复制分页元信息（current/size/total）
     * 2) 把 records（List<WorkOrder>）映射成 List<WorkOrderVO>
     */
    private Page<WorkOrderVO> toVOPage(Page<WorkOrder> entityPage) {
        // 先把分页信息带过去
        Page<WorkOrderVO> voPage = new Page<>(
                entityPage.getCurrent(),
                entityPage.getSize(),
                entityPage.getTotal()
        );

        // 再把每条记录 entity -> VO
        voPage.setRecords(
                entityPage.getRecords().stream()
                        .map(this::toVO)
                        .collect(Collectors.toList())
        );

        return voPage;
    }
}
