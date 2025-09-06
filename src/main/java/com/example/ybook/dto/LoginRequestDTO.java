package com.example.ybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 登录请求参数
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
@Schema(name = "LoginRequestDTO", description = "登录请求参数")
public class LoginRequestDTO {
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;
}
