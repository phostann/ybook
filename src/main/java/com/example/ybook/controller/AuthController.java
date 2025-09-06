package com.example.ybook.controller;

import com.example.ybook.dto.ChangePasswordRequestDTO;
import com.example.ybook.dto.LoginRequestDTO;
import com.example.ybook.dto.LoginResponse;
import com.example.ybook.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * <p>
 * 认证相关接口
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Tag(name = "认证接口", description = "登录、修改密码等认证相关接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行登录，成功后返回 JWT 令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功，返回令牌"),
            @ApiResponse(responseCode = "400", description = "用户名或密码错误")
    })
    public com.example.ybook.common.ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        return com.example.ybook.common.ApiResponse.success(authService.login(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "登录状态下修改当前用户密码")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "400", description = "旧密码不正确")
    })
    public com.example.ybook.common.ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return com.example.ybook.common.ApiResponse.success();
    }
}
