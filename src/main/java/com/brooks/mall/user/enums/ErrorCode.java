package com.brooks.mall.user.enums;


/**
 * 用户错误码枚举类
 *
 * @author Mr.Yang
 * @date 2026/7/31 10:28
 */
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    // 用户模块错误码 (1xxx)
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "账号已被禁用，请联系管理员"),
    USER_INACTIVE(1003, "账号尚未激活，请检查邮箱"),
    USER_STATUS_UNKNOWN(1004, "账号状态无效"),

    // 认证模块错误码 (2xxx)
    TOKEN_EXPIRED(2001, "登录已过期，请重新登录");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}