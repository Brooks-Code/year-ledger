package com.brooks.mall.common.result;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    // 缓存无数据成功响应
    @SuppressWarnings("rawtypes")
    private static final Result EMPTY_SUCCESS = new Result<>(
            ResultCode.SUCCESS.getCode(),
            ResultCode.SUCCESS.getMessageTemplate(),
            null
    );

    private int code;
    private String msg;
    private T data;
    private long ts;

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.ts = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    @SuppressWarnings("unchecked")
    public static <T> Result<T> success() {
        return (Result<T>) EMPTY_SUCCESS;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessageTemplate(), data);
    }

    // ==================== 失败响应 ====================

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessageTemplate(), null);
    }

    public static <T> Result<T> fail(ResultCode resultCode, Object... args) {
        return new Result<>(resultCode.getCode(), resultCode.format(args), null);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
    public long getTs() { return ts; }

    // ==================== 手动实现 NON_NULL 序列化 替代 @JsonInclude ====================
    /**
     * 当使用 Jackson/Fastjson 等框架时，此方法不会被自动调用。
     * 但若在特殊场景下手动转JSON，或配合自定义序列化器，可实现null过滤。
     * 
     * 更推荐的做法：在项目全局Jackson配置中设置NON_NULL，而非在每个类上标注：
     * spring.jackson.default-property-inclusion=non_null
     */
    public Map<String, Object> toNonNullMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("code", code);
        if (msg != null) map.put("msg", msg);
        if (data != null) map.put("data", data);
        map.put("ts", ts);
        return map;
    }
}