package com.example.ybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 修改密码请求参数
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
@Schema(name = "ChangePasswordRequestDTO", description = "修改密码请求参数")
public class ChangePasswordRequestDTO {
    @Schema(description = "旧密码", example = "123456")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", example = "Abcd1234")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
