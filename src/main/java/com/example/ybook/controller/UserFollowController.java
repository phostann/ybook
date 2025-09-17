package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.FollowStatusBatchDTO;
import com.example.ybook.service.UserFollowService;
import com.example.ybook.vo.FollowStatusVO;
import com.example.ybook.vo.FollowStatsVO;
import com.example.ybook.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户关注控制器
 */
@Tag(name = "用户关注", description = "用户关注、粉丝等功能")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/users")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @Operation(summary = "关注/取消关注用户", description = "切换对指定用户的关注状态")
    @PostMapping("/{userId}/follow")
    public ApiResult<Boolean> toggleFollow(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        boolean isFollowing = userFollowService.toggleFollow(userId);
        return ApiResult.success(isFollowing);
    }

    @Operation(summary = "获取关注状态", description = "获取当前用户对指定用户的关注状态")
    @GetMapping("/{userId}/follow-status")
    public ApiResult<Boolean> getFollowStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        boolean isFollowing = userFollowService.getFollowStatus(userId, null);
        return ApiResult.success(isFollowing);
    }

    @Operation(summary = "批量获取关注状态", description = "批量获取当前用户对多个用户的关注状态")
    @PostMapping("/batch-follow-status")
    public ApiResult<List<FollowStatusVO>> batchGetFollowStatus(
            @Valid @RequestBody FollowStatusBatchDTO dto) {
        List<FollowStatusVO> statusList = userFollowService.batchGetFollowStatus(dto.getUserIds(), null);
        return ApiResult.success(statusList);
    }

    @Operation(summary = "获取关注列表", description = "获取当前用户的关注列表（我关注的人）")
    @GetMapping("/following")
    public ApiResult<PageResult<UserVO>> getFollowing(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserVO> page = userFollowService.getUserFollowing(null, pageNum, pageSize);
        PageResult<UserVO> result = PageResult.<UserVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
        return ApiResult.success(result);
    }

    @Operation(summary = "获取粉丝列表", description = "获取当前用户的粉丝列表（关注我的人）")
    @GetMapping("/followers")
    public ApiResult<PageResult<UserVO>> getFollowers(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserVO> page = userFollowService.getUserFollowers(null, pageNum, pageSize);
        PageResult<UserVO> result = PageResult.<UserVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
        return ApiResult.success(result);
    }

    @Operation(summary = "获取指定用户的关注列表", description = "获取指定用户的关注列表（TA关注的人）")
    @GetMapping("/{userId}/following")
    public ApiResult<PageResult<UserVO>> getUserFollowing(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserVO> page = userFollowService.getUserFollowing(userId, pageNum, pageSize);
        PageResult<UserVO> result = PageResult.<UserVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
        return ApiResult.success(result);
    }

    @Operation(summary = "获取指定用户的粉丝列表", description = "获取指定用户的粉丝列表（关注TA的人）")
    @GetMapping("/{userId}/followers")
    public ApiResult<PageResult<UserVO>> getUserFollowers(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int pageSize) {
        Page<UserVO> page = userFollowService.getUserFollowers(userId, pageNum, pageSize);
        PageResult<UserVO> result = PageResult.<UserVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
        return ApiResult.success(result);
    }

    @Operation(summary = "获取关注统计", description = "获取指定用户的关注统计信息")
    @GetMapping("/{userId}/follow-stats")
    public ApiResult<FollowStatsVO> getFollowStats(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        FollowStatsVO stats = userFollowService.getFollowStats(userId);
        return ApiResult.success(stats);
    }
}