package com.example.ybook.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 统一返回体（避免与 Swagger 的 ApiResponse 注解重名）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "统一返回体")
public class ApiResult<T> {
    @Schema(description = "业务状态码", example = "0")
    private int code;
    @Schema(description = "消息描述", example = "OK")
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "响应数据")
    private T data;
    @Schema(description = "时间戳（毫秒）", example = "1712345678901")
    private long timestamp;

    public static <T> ApiResult<T> success(T data) {
        return ApiResult.<T>builder()
                .code(ApiCode.SUCCESS.getCode())
                .message(ApiCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResult<T> success() {
        return success(null);
    }

    public static <T> ApiResult<T> error(ApiCode code, String message) {
        return ApiResult.<T>builder()
                .code(code.getCode())
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResult<T> error(ApiCode code) {
        return error(code, code.getMessage());
    }
}

