package com.brooks.mall.user.dto.request;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 日记创建/修改请求 DTO
 */
@Data
public class LifeDiaryRequest {

    /**
     * 主键ID（更新时必传，新增时不传）
     */
    private Long id;

    /**
     * 日记日期
     * ⚠️ 注意：数据库是 date 类型，这里用 LocalDate
     */
    @NotNull(message = "日记日期不能为空")
    private LocalDate diaryDate;

    /**
     * 日记标题
     */
    @NotBlank(message = "标题不能为空")
    private String title;

    /**
     * 日记正文
     */
    @NotBlank(message = "内容不能为空")
    private String content;

    // 注意：created_at, updated_at, created_by 等字段通常不由前端传递，
    // 而是在 Service 层根据当前登录用户和系统时间自动填充。
}