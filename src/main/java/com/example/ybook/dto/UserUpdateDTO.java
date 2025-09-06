package com.example.ybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 用户更新DTO (不包含密码和状态字段)
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
@Schema(name = "UserUpdateDTO", description = "用户更新参数（不包含密码和状态）")
public class UserUpdateDTO {
    @Schema(description = "用户名", example = "alice")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;
    
    @Schema(description = "邮箱", example = "alice@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.png")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    @Schema(description = "性别：0 未知；1 男；2 女", example = "2")
    private String gender; // 0: unknown; 1: 男; 2: 女
}
