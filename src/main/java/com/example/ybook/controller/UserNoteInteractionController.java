package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.InteractionStatusDTO;
import com.example.ybook.service.UserNoteInteractionService;
import com.example.ybook.vo.InteractionStatusVO;
import com.example.ybook.vo.NoteVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 用户笔记交互控制器
 */
@Tag(name = "用户笔记交互", description = "笔记点赞、收藏等交互功能")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notes")
public class UserNoteInteractionController {

    private final UserNoteInteractionService userNoteInteractionService;

    public UserNoteInteractionController(UserNoteInteractionService userNoteInteractionService) {
        this.userNoteInteractionService = userNoteInteractionService;
    }

    @Operation(summary = "点赞/取消点赞笔记", description = "切换用户对笔记的点赞状态")
    @PostMapping("/{id}/like")
    public ApiResult<Boolean> toggleLike(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long id) {
        boolean isLiked = userNoteInteractionService.toggleLike(id);
        return ApiResult.success(isLiked);
    }

    @Operation(summary = "收藏/取消收藏笔记", description = "切换用户对笔记的收藏状态")
    @PostMapping("/{id}/favorite")
    public ApiResult<Boolean> toggleFavorite(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long id) {
        boolean isFavorited = userNoteInteractionService.toggleFavorite(id);
        return ApiResult.success(isFavorited);
    }

    @Operation(summary = "获取笔记交互状态", description = "获取用户对指定笔记的交互状态")
    @GetMapping("/{id}/interaction-status")
    public ApiResult<InteractionStatusVO.InteractionDetailVO> getInteractionStatus(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long id) {
        InteractionStatusVO.InteractionDetailVO status = 
            userNoteInteractionService.getInteractionStatus(id, null);
        return ApiResult.success(status);
    }

    @Operation(summary = "批量获取笔记交互状态", description = "批量获取用户对多个笔记的交互状态")
    @GetMapping("/batch-interaction-status")
    public ApiResult<InteractionStatusVO> batchGetInteractionStatus(
            @Parameter(description = "笔记ID列表，用逗号分隔", example = "1,2,3", required = true)
            @RequestParam("noteIds") String noteIdsStr) {
        
        // 解析逗号分隔的ID字符串
        java.util.List<Long> noteIds;
        try {
            noteIds = java.util.Arrays.stream(noteIdsStr.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(java.util.stream.Collectors.toList());
        } catch (NumberFormatException e) {
            throw new com.example.ybook.exception.BizException(com.example.ybook.common.ApiCode.PARAM_INVALID);
        }
        
        if (noteIds.isEmpty()) {
            throw new com.example.ybook.exception.BizException(com.example.ybook.common.ApiCode.PARAM_EMPTY);
        }
        
        InteractionStatusVO statusMap = 
            userNoteInteractionService.batchGetInteractionStatus(noteIds, null);
        return ApiResult.success(statusMap);
    }

    @Operation(summary = "获取用户收藏的笔记列表", description = "分页查询当前用户收藏的笔记")
    @GetMapping("/favorites")
    public ApiResult<PageResult<NoteVO>> getUserFavoriteNotes(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size) {
        Page<NoteVO> page = userNoteInteractionService.getUserFavoriteNotes(null, (int) current, (int) size);
        
        PageResult<NoteVO> result = PageResult.<NoteVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
                
        return ApiResult.success(result);
    }

    @Operation(summary = "获取用户点赞的笔记列表", description = "分页查询当前用户点赞的笔记")
    @GetMapping("/likes")
    public ApiResult<PageResult<NoteVO>> getUserLikedNotes(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") long size) {
        Page<NoteVO> page = userNoteInteractionService.getUserLikedNotes(null, (int) current, (int) size);
        
        PageResult<NoteVO> result = PageResult.<NoteVO>builder()
                .current(page.getCurrent())
                .size(page.getSize())
                .total(page.getTotal())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
                
        return ApiResult.success(result);
    }
}