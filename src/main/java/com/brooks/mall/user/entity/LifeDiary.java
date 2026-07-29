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
     * 注意：使用 Long 类型接收雪花ID
     */
    private Long id;

    /**
     * 日记日期
     * 对应数据库 date 类型
     */
    private LocalDate diaryDate;

    /**
     * 日记标题
     */
    private String title;

    /**
     * 日记正文
     * 对应数据库 text 类型
     */
    private String content;

    /**
     * 创建时间
     * 数据库默认 CURRENT_TIMESTAMP，但建议Java层也赋值以保持一致性
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 由Java代码维护
     */
    private LocalDateTime updatedAt;
}