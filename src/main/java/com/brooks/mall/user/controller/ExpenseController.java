package com.brooks.mall.user.controller;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.common.result.ResultCode;
import com.brooks.mall.user.dto.ExpenseRecordDTO;
import com.brooks.mall.user.service.impl.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/29 13:26
 */
@RestController // 返回 JSON 数据
@RequestMapping("/api") // 对应前端的 /api 代理前缀
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    /**
     * 新增账单记录
     * 对应前端: axios.post('/api/expense-record', form)
     */
    @PostMapping("/expense-record")
    public Result saveExpense(@RequestBody ExpenseRecordDTO record) {
        // 调用 service 层保存到数据库
        boolean success = expenseService.save(record);

        if (success) {
            return Result.success("保存成功");
        } else {
            return Result.fail(ResultCode.INTERNAL_ERROR.getCode(),"保存失败");
        }
    }
}
