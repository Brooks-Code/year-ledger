package com.brooks.mall.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应数据
 */
@Data
@AllArgsConstructor
public class LoginResponse implements Serializable {
    private String token;      // JWT Token
    private String userid;     // 业务用户ID
    private String username;
    private Integer status;
}