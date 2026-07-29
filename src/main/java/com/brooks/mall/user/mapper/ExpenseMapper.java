package com.brooks.mall.user.mapper;

import com.brooks.mall.user.entity.ExpenseRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/29 13:29
 */
@Mapper
public interface ExpenseMapper {
    /**
     * 新增账单记录
     *
     * @param entity
     * @return
     */
    int insert(ExpenseRecord entity);
}
