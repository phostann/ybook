package com.example.ybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 用户创建DTO
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
public class UserCreateDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String password;
    
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    private String gender; // 0: unknown; 1: 男; 2: 女

    @Size(max = 20, message = "手机号码长度不能超过20个字符")
    private String phone;
}