package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.LoginRequest;
import com.brooks.mall.user.dto.LoginResponse;
import com.brooks.mall.user.dto.UserRegisterRequest;
import com.brooks.mall.user.entity.User;

import java.util.List;

/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/28 16:45
 */
public interface UserService {
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
    /**
     * 获取用户列表
     */
    List<User> getUsers();
}