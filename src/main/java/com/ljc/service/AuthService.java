package com.ljc.service;

import com.ljc.dto.LoginReq;
import com.ljc.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginReq req);

}
