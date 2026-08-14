package com.brooks.mall.user.dto.request;

import lombok.Data;
import java.time.LocalDate;

/**
 * 日记查询请求 DTO
 */
@Data
public class LifeDiaryQueryRequest {

    /**
     * 页码（默认第1页）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10条）
     */
    private Integer pageSize = 10;

    /**
     * 开始日期（可选）
     */
    private LocalDate startDate;

    /**
     * 结束日期（可选）
     */
    private LocalDate endDate;

    /**
     * 关键词搜索（可选，搜索标题或内容）
     */
    private String keyword;
}