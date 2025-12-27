package com.ljc.config;

import com.ljc.common.BizException;
import com.ljc.common.context.AuthContext;
import com.ljc.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        String uri = request.getRequestURI();

        //放行 OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 1️⃣ 登录接口直接放行
        if ("/api/login".equals(uri)) {
            return true;
        }

        // 2️⃣ 取 Authorization 头
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }

        String token = auth.substring(7);

        try {
            // 3️⃣ JWT 校验 + 解析
            Claims claims = JwtUtil.parseToken(token);

            // 4️⃣ 写入 AuthContext（ThreadLocal）
            AuthContext.setCompanyId(((Number) claims.get("companyId")).longValue());
            AuthContext.setEmployeeId(((Number) claims.get("employeeId")).longValue());
            AuthContext.setName((String) claims.get("name"));
            AuthContext.setRole((String) claims.get("role"));

            return true;
        } catch (Exception e) {
            throw new BizException(401, "token 无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        // 5️⃣ 非常重要：清理 ThreadLocal
        AuthContext.clear();
    }
}
