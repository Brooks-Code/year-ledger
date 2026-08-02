package com.brooks.mall.user.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类
 * 作用：创建、解析、验证 JWT
 * @Author Brooks Cole
 * @Date 2026/7/22 10:29
 */
@Component
public class JwtUtil {

    /**
     * 从配置文件读取密钥，生产环境务必使用至少256位的随机字符串
     * 例如: mySecretKeyForBrooksMallUserModuleMustBeAtLeast256BitsLong!!
     */
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时(毫秒)
    private long expiration;

    /**
     * 获取签名密钥 (HMAC-SHA256)
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 Token
     * @param userId 数据库主键ID (作为JWT的subject)
     * @param claims 自定义声明 (如 userid, username, status)
     */
    public String generateToken(Long userId, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析并验证 Token
     * @return Claims 包含所有payload信息
     * @throws Exception token过期、签名无效时会自动抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从Token中获取数据库主键ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从Token中获取业务userid
     */
    public String getBusinessUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userid", String.class);
    }
}