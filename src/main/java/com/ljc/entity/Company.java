package com.ljc.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("company") // 告诉程序：这个类对应数据库里叫 "company" 的表
public class Company {

    @TableId // 告诉程序：这个字段是主键

    private Long id;
    private String name;
}
