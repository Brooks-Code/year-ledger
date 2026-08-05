package com.brooks.mall.user.interceptor;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.common.result.ResultCode;
import com.brooks.mall.user.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

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
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 验证通过，放行
            return true;

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