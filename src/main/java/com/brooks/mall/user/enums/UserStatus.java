package com.brooks.mall.user.enums;

/**
 * TODO
 *
 * @Description 枚举类: 用户状态
 * @Author Brooks Cole
 * @Date 2026/7/31 14:30
 */
public enum UserStatus {
    DISABLED(0),
    ACTIVE(1),
    INACTIVE(2);

    private final int code;

    UserStatus(int code) {
        this.code = code;
    }

    public static UserStatus fromCode(Integer code) {
        if (code == null) return null;
        for (UserStatus s : values()) {
            if (s.code == code) return s;
        }
        return null;
    }
}