package com.example.ybook.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 全局统一返回体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiResponse", description = "统一返回体")
public class ApiResponse<T> {
    @Schema(description = "业务状态码", example = "0")
    private int code;
    @Schema(description = "消息描述", example = "OK")
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "响应数据")
    private T data;
    @Schema(description = "时间戳（毫秒）", example = "1712345678901")
    private long timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(ApiCode.SUCCESS.getCode())
                .message(ApiCode.SUCCESS.getMessage())
                .data(data)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> error(ApiCode code, String message) {
        return ApiResponse.<T>builder()
                .code(code.getCode())
                .message(message)
                .timestamp(Instant.now().toEpochMilli())
                .build();
    }

    public static <T> ApiResponse<T> error(ApiCode code) {
        return error(code, code.getMessage());
    }
}
