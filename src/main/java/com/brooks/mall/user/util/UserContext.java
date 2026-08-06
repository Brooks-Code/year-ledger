package com.brooks.mall.user.util;

import com.brooks.mall.user.entity.User;

/**
 * 用户上下文工具类
 * 用于在当前线程中存储和获取当前登录用户的ID
 */
public class UserContext {
    // 使用 ThreadLocal 存储完整的 User 对象
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户对象
     */
    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取当前用户对象
     */
    public static User getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        User user = USER_HOLDER.get();
        return user != null ? user.getId() : null;
    }

    /**
     * 清除当前用户ID (必须在请求结束时调用，防止内存泄漏)
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
}