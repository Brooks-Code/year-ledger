package com.brooks.mall.user.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 生活日记表实体类
 */
@Data
public class LifeDiary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（雪花算法生成）
     */
    private Long id;

    /**
     * 创建时间
     * 对应数据库 timestamp，通常由数据库自动维护
     */
    private LocalDateTime createdAt;

    /**
     * 创建人ID
     */
    private String createdBy;

    /**
     * 更新时间
     * 对应数据库 datetime，需手动设置
     */
    private LocalDateTime updatedAt;

    /**
     * 更新人ID
     */
    private String updatedBy;

    /**
     * 日记日期
     * 注意：数据库是 date 类型，Java 推荐用 LocalDate
     */
    private LocalDate diaryDate;

    /**
     * 日记标题
     */
    private String title;

    /**
     * 日记正文
     */
    private String content;

}