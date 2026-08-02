package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.LoginRequest;
import com.brooks.mall.user.dto.LoginResponse;
import com.brooks.mall.user.dto.UserRegisterRequest;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/2 20:44
 */
public interface AuthService {
    /**
     * 新增：用户注册接口
     */
    void register(UserRegisterRequest request);
    /**
     * 登录
     *
     * @param request 登录请求参数
     * @return 登录响应数据
     */
    LoginResponse login(LoginRequest request);
}
