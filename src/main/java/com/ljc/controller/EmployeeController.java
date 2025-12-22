package com.ljc.controller;

import com.ljc.common.Result;
import com.ljc.entity.Employee;
import com.ljc.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping({"/employee", "/api/employees"})
    public Result<List<Employee>> listEmployees() {
        return Result.success(employeeService.list());
    }
}
