package com.example.ybook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * 登录响应结果
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
}
