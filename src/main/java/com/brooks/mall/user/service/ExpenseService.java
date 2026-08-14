package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.request.ExpenseRecordRequest;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/29 13:25
 */
public interface ExpenseService {
    /**
     * 新增账单记录
     *
     * @param record
     * @return
     */
    boolean save(ExpenseRecordRequest record);
}
