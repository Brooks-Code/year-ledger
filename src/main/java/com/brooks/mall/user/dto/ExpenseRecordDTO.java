package com.brooks.mall.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 记账表单请求对象 (DTO)
 * 对应前端"记一笔日常花销"表单
 */
@Data
public class ExpenseRecordDTO {

    /**
     * 花销金额
     * 对应表单：* 花销金额
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /**
     * 消费分类
     * 对应表单：* 消费分类 (下拉选择)
     */
    @NotBlank(message = "消费分类不能为空")
    private String category;

    /**
     * 消费日期
     * 对应表单：* 消费日期 (日期选择器)
     */
    @NotNull(message = "消费日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expenseDate;

    /**
     * 支付方式
     * 对应表单：* 支付方式 (单选: 微信/支付宝/现金/信用卡)
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /**
     * 简短备注
     * 对应表单：简短备注 (选填, 最大255字)
     */
    // 注意：因为是选填，所以不加 @NotBlank 或 @NotNull
        private String remark;
}