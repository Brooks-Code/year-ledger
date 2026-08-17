package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.request.LoginRequest;
import com.brooks.mall.user.dto.request.RegisterRequest;
import com.brooks.mall.user.dto.response.LoginResponse;
import com.brooks.mall.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

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
    void register(RegisterRequest request);
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

    /**
     * 根据id查询用户
     */
    User getUser(Long id);

    /**
     * 处理本地头像上传业务 --作废
     * @param file 前端传来的文件
     * @return 图片的访问 URL
     */
    String uploadAvatar(MultipartFile file);
}