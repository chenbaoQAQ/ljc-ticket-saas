package com.ljc.controller;

import com.ljc.common.Result;
import com.ljc.dto.LoginReq;
import com.ljc.service.AuthService;
import com.ljc.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录（返回 token）
     *
     * POST /api/login
     */
    @PostMapping("/api/login")
    public Result<LoginVO> login(@RequestBody LoginReq req) {
        LoginVO vo = authService.login(req);
        return Result.success(vo);
    }

}
