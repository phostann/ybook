package com.example.ybook.common;

import lombok.Getter;

/**
 * 统一业务状态码定义
 */
@Getter
public enum ApiCode {
    // 通用
    SUCCESS(0, "OK"),
    BAD_REQUEST(40000, "Bad request"),
    PARAM_MISSING(40001, "缺少必填参数"),
    PARAM_TYPE_MISMATCH(40002, "参数类型错误"),
    REQUEST_NOT_READABLE(40003, "请求体解析失败"),
    VALIDATION_ERROR(42200, "Validation error"),
    NOT_FOUND(40400, "Not found"),
    MEDIA_TYPE_NOT_ACCEPTABLE(40600, "不可接受的媒体类型"),
    INTERNAL_ERROR(50000, "Internal server error"),
    METHOD_NOT_ALLOWED(40500, "Method not allowed"),
    MEDIA_TYPE_NOT_SUPPORTED(41500, "不支持的媒体类型"),

    // 认证/鉴权
    UNAUTHORIZED(40100, "未认证"),
    FORBIDDEN(40300, "无权限"),
    TOKEN_INVALID(40101, "无效令牌"),
    TOKEN_EXPIRED(40102, "令牌已过期"),

    // 用户相关 100xx
    USER_NOT_FOUND(10001, "用户不存在"),
    USERNAME_EXISTS(10002, "用户名已存在"),
    EMAIL_EXISTS(10003, "邮箱已存在"),
    USER_DISABLED(10004, "用户已禁用");

    private final int code;
    private final String message;

    ApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
