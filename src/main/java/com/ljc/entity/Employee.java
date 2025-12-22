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
@TableName("employee")
public class Employee {

    @TableId
    private Long id;

    private String name;

    /** 角色：比如 ADMIN / AGENT / USER（先用字符串，后面可升级为 RBAC 表） */
    private String role;

    @TableField("company_id")
    private Long companyId;

    private String phone;
}
