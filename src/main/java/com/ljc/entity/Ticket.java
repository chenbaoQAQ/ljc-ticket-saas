package com.ljc.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ticket") // 对应数据库里的 ticket 表
public class Ticket {

    @TableId
    private Long id;

    @TableField("work_order_id")
    private Long workOrderId;

    private String description;

    @TableField("create_time")
    private LocalDateTime createTime;
}
