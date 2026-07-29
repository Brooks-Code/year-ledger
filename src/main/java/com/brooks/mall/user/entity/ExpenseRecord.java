package com.brooks.mall.user.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日常花销记录表实体类
 */
@Data
public class ExpenseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（雪花算法生成）
     * 对应数据库 bigint(20)
     */
    private Long id;

    /**
     * 花销金额，保留两位小数
     * 对应数据库 decimal(10,2)，必须使用 BigDecimal 避免精度丢失
     */
    private BigDecimal amount;

    /**
     * 消费分类，如：餐饮、交通、娱乐、购物
     */
    private String category;

    /**
     * 消费日期（方便按天/月统计）
     * 对应数据库 date 类型
     */
    private LocalDate expenseDate;

    /**
     * 简短备注，如：和同事吃火锅
     */
    private String description;

    /**
     * 支付方式：微信、支付宝、现金、信用卡
     */
    private String paymentMethod;

    /**
     * 记录创建时间
     * 对应数据库 timestamp，由数据库自动维护默认值
     */
    private LocalDateTime createdAt;
}