package com.ljc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljc.common.BizException;
import com.ljc.dto.LoginReq;
import com.ljc.entity.Employee;
import com.ljc.service.AuthService;
import com.ljc.service.EmployeeService;
import com.ljc.util.JwtUtil;
import com.ljc.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmployeeService employeeService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginVO login(LoginReq req) {

        if (req == null) {
            throw new BizException("请求不能为空");
        }
        if (req.getUsername() == null || req.getUsername().trim().isEmpty()) {
            throw new BizException("username 不能为空");
        }
        if (req.getPassword() == null || req.getPassword().trim().isEmpty()) {
            throw new BizException("password 不能为空");
        }

        Employee employee = employeeService.getOne(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUsername, req.getUsername())
                        .last("limit 1")
        );

        if (employee == null) {
            throw new BizException("用户名或密码错误");
        }

        String dbHash = employee.getPasswordHash();
        if (dbHash == null || dbHash.trim().isEmpty()) {
            throw new BizException("账号未设置密码");
        }

        boolean ok = passwordEncoder.matches(req.getPassword(), dbHash);
        if (!ok) {
            throw new BizException("用户名或密码错误");
        }

        // ====== 生成 JWT ======
        Map<String, Object> claims = new HashMap<>();
        claims.put("employeeId", employee.getId());
        claims.put("companyId", employee.getCompanyId());
        claims.put("name", employee.getName());
        claims.put("role", employee.getRole());

        String token = JwtUtil.generateToken(claims);// TODO: 改成你 JwtUtil 的真实方法名
        //claims 是写进 token 里的，而 VO 是返回给前端的，它们用途完全不同

        // ====== 返回 VO ======
        LoginVO vo = new LoginVO();
        vo.setEmployeeId(employee.getId());
        vo.setCompanyId(employee.getCompanyId());
        vo.setName(employee.getName());
        vo.setRole(employee.getRole());
        vo.setToken(token);

        return vo;
    }

}
