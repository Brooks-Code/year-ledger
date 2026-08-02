package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.LoginRequest;
import com.brooks.mall.user.dto.LoginResponse;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.exception.BusinessException;
import com.brooks.mall.user.mapper.UserMapper;
import com.brooks.mall.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    // 假设你已有 JwtUtil 和 PasswordEncoder (如BCrypt)
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户（已过滤逻辑删除）
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            // 安全最佳实践：不提示"用户不存在"，统一提示
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 校验账号状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            String msg;
            switch (user.getStatus()) {
                case 0:
                    msg = "账号已被禁用，请联系管理员";
                    break;
                case 2:
                    msg = "账号尚未激活，请检查邮箱完成激活";
                    break;
                default:
                    msg = "账号状态异常";
                    break;
            }
            throw new BusinessException(msg);
        }

        // 3. 校验密码（使用BCrypt等单向加密比对）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成JWT Token（建议将userid和status放入claims）
        Map<String, Object> claims = new HashMap<>(4); // 指定初始容量避免扩容
        claims.put("userid", user.getUserid());
        claims.put("username", user.getUsername());
        claims.put("status", user.getStatus());
        String token = jwtUtil.generateToken(user.getId(), claims);

        // 5. 可选：更新最后登录时间/记录登录日志
        // userMapper.updateLastLogin(user.getId(), LocalDateTime.now());

        log.info("用户登录成功: userid={}", user.getUserid());
        return new LoginResponse(token, user.getUserid(), user.getUsername(), user.getStatus());
    }
}