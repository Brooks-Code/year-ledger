package com.brooks.mall.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求参数
 */
@Data
public class LoginRequest implements Serializable {
    @NotBlank(message = "用户名不能为空")
    private String userId;

    @NotBlank(message = "密码不能为空")
    private String password;
}