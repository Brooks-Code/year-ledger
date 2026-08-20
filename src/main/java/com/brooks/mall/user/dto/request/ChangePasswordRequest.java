package com.brooks.mall.user.dto.request;

import lombok.Data;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/8/19 11:01
 */
@Data
public class ChangePasswordRequest {
    // 旧密码
    private String oldPassword;
    // 新密码
    private String newPassword;
    // 确认密码
    private String confirmPassword;
}
