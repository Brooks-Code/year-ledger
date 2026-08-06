package com.brooks.mall.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息表实体类
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据库、主键
     */
    private Long id;

    /**
     * 创建时间
     * 注意：原生 MyBatis 不会自动赋值，需在 SQL 或 Service 层手动设置
     */
    private LocalDateTime createdAt;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 业务主键：用户ID (对应数据库字段 userid)
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 用户头像URL
     */
    private String avatar;

    /**
     * 状态: 1-正常, 0-禁用, 2-待激活
     */
    private Integer status;

    /**
     * 删除标记: 0-未删除, 1-已删除
     */
    private Integer isDeleted;

}