package com.ljc.entity;

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
    private String content; // 具体描述
    private String status;  // 状态（待处理/解决中/已关闭）

    // 下面是三个关键的“身份证号”关联
    private Long creatorId; // 创建人 ID
    private Long handlerId; // 处理人 ID
    private Long companyId; // 所属公司 ID
}