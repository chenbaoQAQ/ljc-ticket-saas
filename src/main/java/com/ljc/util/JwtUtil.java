package com.ljc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    // TODO: 先写死，后面你再搬到 application.yml
    private static final String SECRET = "ljc-ticket-saas-secret-key-change-me";
    private static final long EXPIRE_MILLIS = 24L * 60 * 60 * 1000; // 24h

    private JwtUtil() {
    }

    /**
     * 生成 token
     */
    public static String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXPIRE_MILLIS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, SECRET.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * 解析 token，解析失败会抛异常
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }

}
