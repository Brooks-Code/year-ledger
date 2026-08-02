package com.brooks.mall.user.handler;

import com.brooks.mall.common.result.Result;
import com.brooks.mall.user.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO
 *  全局异常处理
 *
 * @Author Brooks Cole
 * @Date 2026/7/31 10:28
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusiness(BusinessException e) {
        return Result.fail(200, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleSystem(Exception e) {
        return Result.fail(500, "服务器内部错误");
    }
}