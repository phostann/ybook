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
    PARAM_INVALID(40004, "参数格式不正确"),
    PARAM_EMPTY(40005, "参数不能为空"),
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
    USER_DISABLED(10004, "用户已禁用"),
    USER_LOCKED(10005, "账户已锁定"),
    USER_EXPIRED(10006, "账户已过期"),
    USER_CREDENTIALS_EXPIRED(10007, "凭证已过期"),
    LOGIN_FAILED(10008, "用户名或密码错误"),
    OLD_PASSWORD_INCORRECT(10009, "旧密码不正确"),
    USER_NOT_LOGGED_IN(10010, "用户未登录"),

    // 标签相关 200xx
    LABEL_NOT_FOUND(20001, "标签不存在"),
    LABEL_NAME_EXISTS(20002, "标签名称已存在"),
    
    // 笔记相关 300xx
    NOTE_NOT_FOUND(30001, "笔记不存在"),
    NOTE_ACCESS_DENIED(30002, "无权访问该笔记"),
    NOTE_STATUS_INVALID(30003, "笔记状态无效"),
    
    // 评论相关 400xx
    COMMENT_NOT_FOUND(40001, "评论不存在"),
    COMMENT_ACCESS_DENIED(40002, "无权限编辑此评论"),
    COMMENT_DELETE_DENIED(40003, "无权限删除此评论"),
    ROOT_COMMENT_NOT_FOUND(40004, "根评论不存在"),
    REPLY_COMMENT_NOT_FOUND(40005, "被回复的评论不存在"),
    COMMENT_NOTE_MISMATCH(40006, "评论与笔记不匹配"),
    
    // 文件相关 500xx
    FILE_EMPTY(50001, "文件不能为空"),
    FILE_NAME_EMPTY(50002, "文件名不能为空"),
    FILE_UPLOAD_FAILED(50003, "文件上传失败"),
    FILE_DELETE_FAILED(50004, "文件删除失败"),
    FILE_NOT_FOUND(50005, "文件不存在"),
    FILE_SIZE_EXCEEDED(50006, "文件大小超出限制"),
    FILE_TYPE_NOT_SUPPORTED(50007, "不支持的文件类型");

    private final int code;
    private final String message;

    ApiCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
