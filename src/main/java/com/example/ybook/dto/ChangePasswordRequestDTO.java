package com.example.ybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * <p>
 * 修改密码请求参数
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
public class ChangePasswordRequestDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
