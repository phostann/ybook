package com.example.ybook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 登录响应结果
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
@Schema(name = "LoginResponse", description = "登录响应结果")
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT 令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
