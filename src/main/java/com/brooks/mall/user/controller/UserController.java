package com.brooks.mall.user.controller;

import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/22 10:29
 */
@RestController // 返回 JSON 数据
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public List<User> listUsers() {
        return userService.getUsers();
    }
}