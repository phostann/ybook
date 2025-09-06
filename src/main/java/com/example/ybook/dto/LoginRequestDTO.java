package com.example.ybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 登录请求参数
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
public class LoginRequestDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
