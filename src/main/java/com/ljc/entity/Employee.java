package com.ljc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("Employee")
public class Employee {

    @TableId

    private Long id;
    private String name;
    private String role;// 角色：比如 ADMIN 或 USER
    private Long companyId; // 💡 这里就是关联的公司 ID
    private String phone;
}
