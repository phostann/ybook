package com.example.ybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 用户创建DTO
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
@Schema(name = "UserCreateDTO", description = "用户创建参数")
public class UserCreateDTO {
    @Schema(description = "用户名", example = "alice", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;
    
    @Schema(description = "邮箱", example = "alice@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Schema(description = "密码", example = "Abcd1234", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "头像不能为空")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    @Schema(description = "昵称", example = "小爱同学")
    @Size(max = 20, message = "昵称长度不能超过20个字符")
    private String nickname;
    
    @Schema(description = "性别：0 未知；1 男；2 女", example = "1")
    private String gender; // 0: unknown; 1: 男; 2: 女

    @Schema(description = "手机号", example = "13800000000")
    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;
}
