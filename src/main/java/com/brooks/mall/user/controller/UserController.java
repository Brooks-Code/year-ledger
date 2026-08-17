package com.brooks.mall.user.controller;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/22 10:29
 */
@RestController // 返回 JSON 数据
@RequestMapping("/api/user") // 对应前端的 /api 代理前缀
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 处理头像上传业务 -- 作废
     *
     * @param file 前端传来的文件
     * @return 图片的访问 URL
     */
    @PostMapping("/upload/avatar")
    public Result<Map> updateAvatar(@RequestParam("file") MultipartFile file) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            // 1. 基础校验
            if (file.isEmpty()) {
                return Result.fail(500, "上传文件不能为空");
            }
            // 2. 调用 Service 层处理上传逻辑
            String avatarUrl = userService.uploadAvatar(file);
            map.put("avatarUrl", avatarUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail(500, "上传失败");
        }
        return Result.success(map);
    }


    @GetMapping("/users")
    public List<User> listUsers() {
        return userService.getUsers();
    }

}