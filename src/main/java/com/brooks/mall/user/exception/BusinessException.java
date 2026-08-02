package com.brooks.mall.user.exception;

import com.brooks.mall.user.enums.ErrorCode;
import lombok.Getter;

/**
 * TODO
 *  业务异常
 * @Author Brooks Cole
 * @Date 2026/7/31 10:28
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 400; // 默认业务错误码
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    // 可选：支持枚举错误码
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}