package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.UserCreateDTO;
import com.example.ybook.dto.UserUpdateDTO;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.service.UserService;
import com.example.ybook.vo.UserVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:57:34
 */
@Tag(name = "用户管理接口", description = "用户的增删改查接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "返回所有用户的精简信息列表")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public com.example.ybook.common.ApiResponse<List<UserVO>> listUsers() {
        return com.example.ybook.common.ApiResponse.success(userService.listAllUsers());
    }

    /**
     * 分页获取用户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取用户列表", description = "按页返回用户列表")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public com.example.ybook.common.ApiResponse<PageResult<UserVO>> pageUsers(
            @Parameter(description = "当前页码", example = "1")
            @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") long size) {
        Page<UserEntity> page = new Page<>(current, size);
        return com.example.ybook.common.ApiResponse.success(userService.pageUsers(page));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "资源不存在")
    })
    public com.example.ybook.common.ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        return com.example.ybook.common.ApiResponse.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户并返回其信息")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public com.example.ybook.common.ApiResponse<UserVO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return com.example.ybook.common.ApiResponse.success(userService.createUser(userCreateDTO));
    }

    /**
     * 更新用户
     */
    @PatchMapping("/{id}")
    @Operation(summary = "更新用户", description = "根据用户ID更新用户的部分信息")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "资源不存在"),
            @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public com.example.ybook.common.ApiResponse<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return com.example.ybook.common.ApiResponse.success(userService.updateUser(id, userUpdateDTO));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "资源不存在")
    })
    public com.example.ybook.common.ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        return com.example.ybook.common.ApiResponse.success(userService.deleteUser(id));
    }
}
