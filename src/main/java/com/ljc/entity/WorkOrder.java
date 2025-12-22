package com.ljc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("work_order")
public class WorkOrder {
    @TableId
    private Long id;

    private String title;   // 工单标题
    private String content; // 工单内容
    private String status;  // 状态（OPEN / IN_PROGRESS / CLOSED 等）

    @TableField("creator_id")
    private Long creatorId; // 创建人 ID

    @TableField("handler_id")
    private Long handlerId; // 处理人 ID（可为空：未指派）

    @TableField("company_id")
    private Long companyId; // 所属公司 ID（单租户阶段也保留，方便后续升级多租户）
}
