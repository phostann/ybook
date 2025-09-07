package com.example.ybook.controller;

import com.example.ybook.common.ApiResult;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.vo.UserVO;

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
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功，返回令牌",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResult.class),
                            examples = @ExampleObject(
                                    name = "成功示例",
                                    description = "返回统一结构，其中 data 为登录令牌",
                                    value = "{\n  \"code\": 0,\n  \"message\": \"OK\",\n  \"data\": { \"token\": \"eyJhbGciOiJIUzI1NiJ9.xxx.yyy\" },\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "用户名或密码错误",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "错误示例",
                                    value = "{\n  \"code\": 40000,\n  \"message\": \"用户名或密码错误\",\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            )
    })
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        return ApiResult.success(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "开放注册接口，创建新用户")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "注册成功",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResult.class),
                            examples = @ExampleObject(
                                    name = "成功示例",
                                    description = "返回统一结构，data 为用户信息（不包含密码）",
                                    value = "{\n  \"code\": 0,\n  \"message\": \"OK\",\n  \"data\": { \n    \"id\": 1,\n    \"username\": \"alice\",\n    \"email\": \"alice@example.com\",\n    \"status\": \"1\"\n  },\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "参数校验失败",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "错误示例",
                                    value = "{\n  \"code\": 42200,\n  \"message\": \"Validation error\",\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            )
    })
    public ApiResult<UserVO> register(@Valid @RequestBody UserCreateDTO request) {
        return ApiResult.success(authService.register(request));
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "登录状态下修改当前用户密码")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "修改成功",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "成功示例",
                                    value = "{\n  \"code\": 0,\n  \"message\": \"OK\",\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未认证或令牌无效",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "未认证示例",
                                    value = "{\n  \"code\": 40100,\n  \"message\": \"未认证\",\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "旧密码不正确",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "错误示例",
                                    value = "{\n  \"code\": 40000,\n  \"message\": \"旧密码不正确\",\n  \"timestamp\": 1712345678901\n}"
                            )
                    )
            )
    })
    public ApiResult<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authService.changePassword(request);
        return ApiResult.success();
    }
}
