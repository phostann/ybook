package com.example.ybook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * <p>
 * 用户更新DTO (不包含密码和状态字段)
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
public class UserUpdateDTO {
    
    @Size(min = 2, max = 50, message = "用户名长度必须在2-50个字符之间")
    private String username;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;
    
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;
    
    private String gender; // 0: unknown; 1: 男; 2: 女
}