package com.ljc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ticket") // 对应数据库里的 ticket 表 📝
public class Ticket {
    @TableId
    private Long id;         // 对应表中的 id
    private String content;  // 对应表中的 content
    private String status;   // 对应表中的 status
}