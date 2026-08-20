package com.brooks.mall.user.interceptor;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.common.result.ResultCode;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.service.UserService;
import com.brooks.mall.user.util.JwtUtil;
import com.brooks.mall.user.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    // ObjectMapper 是线程安全的，应作为单例注入，避免每次请求都 new 一个
    private final ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. OPTIONS 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 提取并校验 Token
        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            renderJson(response, Result.fail(ResultCode.UNAUTHORIZED));
            return false;
        }

        try {
            // 3. 解析 Token 并将用户信息存入 Request
            Long userId = jwtUtil.getUserIdFromToken(token);
                request.setAttribute("userId", userId);

                //将完整对象放入 ThreadLocal
            User user = userService.getUser(userId);
            if (user == null) {
                // 用户不存在或已注销，拦截请求
                response.setStatus(401);
                return false;
            }
            // 4. 将完整对象放入 ThreadLocal
            UserContext.setUser(user);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}, URI: {}", e.getMessage(), request.getRequestURI());
            renderJson(response, Result.fail(ResultCode.TOKEN_EXPIRED));
            return false;
        } catch (JwtException e) {
            log.warn("JWT 格式或签名错误: {}, URI: {}", e.getMessage(), request.getRequestURI());
            renderJson(response, Result.fail(ResultCode.UNAUTHORIZED));
            return false;
        }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后清理 ThreadLocal
        UserContext.clear();
    }

    /**
     * 从 Authorization Header 中解析 Token
     */
    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null; // 不符合 Bearer 规范直接返回 null，不再兼容无前缀的情况
    }

    /**
     * 写入 JSON 响应（使用 try-with-resources 确保流关闭）
     */
    private void renderJson(HttpServletResponse response, Result<?> result) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 显式设置 HTTP 状态码
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}