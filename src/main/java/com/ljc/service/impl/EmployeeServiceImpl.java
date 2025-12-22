package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.entity.Employee;
import com.ljc.mapper.EmployeeMapper;
import com.ljc.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl
        extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

}
