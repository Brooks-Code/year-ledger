package com.brooks.mall.common.result;

public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误: %s"),
    UNAUTHORIZED(401, "未认证或凭证已过期"),
    FORBIDDEN(403, "无权访问该资源"),
    NOT_FOUND(404, "请求的资源不存在"),
    INTERNAL_ERROR(500, "系统内部异常");

    private final int code;
    private final String messageTemplate;

    // 手动构造器替代 @AllArgsConstructor
    ResultCode(int code, String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    // 手动getter替代 @Getter
    public int getCode() {
        return code;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String format(Object... args) {
        if (args == null || args.length == 0) {
            return this.messageTemplate;
        }
        try {
            return String.format(this.messageTemplate, args);
        } catch (Exception e) {
            return this.messageTemplate;
        }
    }
}