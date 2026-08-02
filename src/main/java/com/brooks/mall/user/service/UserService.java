package com.brooks.mall.user.service;

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
     * 获取用户列表
     */
    List<User> getUsers();
}