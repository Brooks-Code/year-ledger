package com.brooks.mall.user.interceptor;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.common.result.ResultCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    // 1. 从 application.properties 中读取密钥
    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是 OPTIONS 请求（跨域预检），直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 2. 获取 Token (通常放在 Header 的 Authorization 字段中)
        String token = request.getHeader("Authorization");

        // 3. 校验 Token 是否存在
        if (!StringUtils.hasText(token)) {
            renderJson(response, Result.fail(ResultCode.UNAUTHORIZED));
            return false;
        }

        try {
            // 4. 解析 Token (验证签名是否匹配 jwt.secret)
            // 注意：如果你的 Token 带有 "Bearer " 前缀，需要截取掉
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解码--获取签名密钥
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            // 解析并验证 Token
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()                      // 构建解析器
                    .parseSignedClaims(token)
                    .getPayload();                // 获取载荷内容

            // 5. (可选) 将用户信息存入请求域，供 Controller 使用
            // 例如：request.setAttribute("userId", claims.getSubject());

            return true; // 验证通过，放行

        } catch (Exception e) {
            // Token 过期或签名错误
            renderJson(response, Result.fail(ResultCode.UNAUTHORIZED));
            return false;
        }
    }

    // 辅助方法：直接返回 JSON 响应
    private void renderJson(HttpServletResponse response, Result<?> result) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(result));
        writer.flush();
        writer.close();
    }
}