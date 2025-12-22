package com.ljc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljc.entity.Employee;

// 这里的 IService 是 MyBatis-Plus 提供的通用业务接口
public interface EmployeeService extends IService<Employee> {
    // 暂时不需要写额外方法，它自带了比 Mapper 更强大的功能
}