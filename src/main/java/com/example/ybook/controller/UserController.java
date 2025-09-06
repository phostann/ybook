package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResponse;
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

/**
 * <p>
 * 用户控制器
 * </p>
 *
 * @author 柒
 * @since 2025-09-03 21:57:34
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping
    public ApiResponse<List<UserVO>> listUsers() {
        return ApiResponse.success(userService.listAllUsers());
    }

    /**
     * 分页获取用户列表
     */
    @GetMapping("/page")
    public ApiResponse<PageResult<UserVO>> pageUsers(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<UserEntity> page = new Page<>(current, size);
        return ApiResponse.success(userService.pageUsers(page));
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public ApiResponse<UserVO> getUserById(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public ApiResponse<UserVO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        return ApiResponse.success(userService.createUser(userCreateDTO));
    }

    /**
     * 更新用户
     */
    @PatchMapping("/{id}")
    public ApiResponse<UserVO> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ApiResponse.success(userService.updateUser(id, userUpdateDTO));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable Long id) {
        return ApiResponse.success(userService.deleteUser(id));
    }
}
