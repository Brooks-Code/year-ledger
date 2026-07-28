package com.brooks.mall.user.service;

import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.mapper.UserMapper;
import com.brooks.mall.user.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/28 16:45
 */
@Service // 必须加这个注解，告诉 Spring 这是一个服务组件
public class UserServiceImpl implements UserService {
    // 自动注入 Mapper
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getUsers() {
        // 调用 Mapper 里的查询方法
        return userMapper.findAll();
    }
}