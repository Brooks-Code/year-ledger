package com.brooks.mall.common.result;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误: %s"),
    UNAUTHORIZED(401, "未认证或凭证已过期"),
    FORBIDDEN(403, "无权访问该资源"),
    NOT_FOUND(404, "请求的资源不存在"),
    INTERNAL_ERROR(500, "系统内部异常");

    private final int code;
    private final String messageTemplate;

    // 静态缓存，避免每次查找都遍历枚举
    private static final Map<Integer, ResultCode> CODE_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(ResultCode::getCode, Function.identity()));

    ResultCode(int code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    public int getCode() {
        return code;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    // 增强format方法，支持null安全 + 占位符数量校验
    public String format(Object... args) {
        if (args == null || args.length == 0) {
            return messageTemplate;
        }
        try {
            // 防止传入null参数导致String.format抛出NullPointerException
            Object[] safeArgs = Arrays.stream(args)
                    .map(arg -> arg == null ? "null" : arg)
                    .toArray();
            return String.format(messageTemplate, safeArgs);
        } catch (Exception e) {
            // 记录日志（实际项目中建议注入Logger）
            System.err.printf("ResultCode格式化失败: template=%s, args=%s, error=%s%n",
                    messageTemplate, Arrays.toString(args), e.getMessage());
            return messageTemplate;
        }
    }

    // 新增根据code反查枚举的方法，全局异常处理器常用
    public static Optional<ResultCode> fromCode(int code) {
        return Optional.ofNullable(CODE_MAP.get(code));
    }

    // 新增默认消息获取方法，避免外部直接操作template
    public String getDefaultMessage() {
        return messageTemplate.contains("%") ? messageTemplate.replace("%s", "").trim() : messageTemplate;
    }
}