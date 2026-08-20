package com.brooks.mall.user.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {
    // 密钥
    @Value("${jwt.secret}")
    private String secret;
    // Token 过期时间 默认 1 天（86400000）
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * 获取签名密钥 (HMAC-SHA256)
     * Base64 解码逻辑，适配配置文件中的 Base64 字符串
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            // 尝试 Base64 解码
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            // 如果解码失败（说明不是 Base64），则直接作为普通字符串处理
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token
     * @param userId 用户 ID
     * @param claims 自定义字段
     */
    public String generateToken(Long userId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(String.valueOf(userId)) // 标准字段存 ID
                .claims(claims)                  // 自定义字段
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析并验证 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取数据库主键 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        // 建议加个空判断或异常处理，防止 subject 为空
        String subject = claims.getSubject();
        if (subject == null) return null;
        return Long.parseLong(subject);
    }

    /**
     * 从 Token 中获取业务 userid
     */
    public String getBusinessUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userid", String.class);
    }
}