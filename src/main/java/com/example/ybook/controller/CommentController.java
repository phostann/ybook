package com.example.ybook.controller;

import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.CommentCreateDTO;
import com.example.ybook.dto.CommentUpdateDTO;
import com.example.ybook.service.CommentService;
import com.example.ybook.vo.CommentListVO;
import com.example.ybook.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理接口（支持多层嵌套回复和分页）
 */
@Tag(name = "评论管理接口", description = "支持多层嵌套回复的评论系统")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Resource
    private CommentService commentService;
    
    @PostMapping
    @Operation(summary = "发表评论", description = "创建新评论或回复评论，支持多层嵌套")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<CommentVO> createComment(@Valid @RequestBody CommentCreateDTO dto) {
        CommentVO commentVO = commentService.createComment(dto);
        return ApiResult.success(commentVO);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新评论", description = "更新评论内容")
    public ApiResult<CommentVO> updateComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id,
            @Valid @RequestBody CommentUpdateDTO dto) {
        CommentVO commentVO = commentService.updateComment(id, dto);
        return ApiResult.success(commentVO);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论", description = "删除指定评论（软删除）")
    public ApiResult<Boolean> deleteComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id) {
        boolean result = commentService.deleteComment(id);
        return ApiResult.success(result);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取评论详情", description = "根据评论ID获取评论详情")
    public ApiResult<CommentVO> getComment(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id) {
        CommentVO commentVO = commentService.getCommentById(id);
        return ApiResult.success(commentVO);
    }
    
    @GetMapping("/note/{noteId}/root")
    @Operation(summary = "获取笔记根评论列表", description = "分页获取指定笔记的根评论列表")
    public ApiResult<PageResult<CommentVO>> getRootCommentsByNoteId(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long noteId,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size) {
        PageResult<CommentVO> result = commentService.getRootCommentsByNoteId(noteId, current, size);
        return ApiResult.success(result);
    }
    
    @GetMapping("/{rootCommentId}/replies")
    @Operation(summary = "获取评论回复列表", description = "分页获取指定根评论的所有回复（扁平化显示）")
    public ApiResult<PageResult<CommentVO>> getRepliesByRootCommentId(
            @Parameter(description = "根评论ID", required = true) @PathVariable Long rootCommentId,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {
        PageResult<CommentVO> result = commentService.getRepliesByRootCommentId(rootCommentId, current, size);
        return ApiResult.success(result);
    }
    
    @GetMapping("/note/{noteId}/stats")
    @Operation(summary = "获取笔记评论统计", description = "获取指定笔记的评论统计信息")
    public ApiResult<CommentListVO> getCommentStatsByNoteId(
            @Parameter(description = "笔记ID", required = true) @PathVariable Long noteId) {
        CommentListVO result = commentService.getCommentStatsByNoteId(noteId);
        return ApiResult.success(result);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户评论列表", description = "分页获取指定用户的评论列表")
    public ApiResult<PageResult<CommentVO>> getCommentsByUserId(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size) {
        PageResult<CommentVO> result = commentService.getCommentsByUserId(userId, current, size);
        return ApiResult.success(result);
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞/取消点赞评论", description = "切换用户对评论的点赞状态")
    public ApiResult<Boolean> toggleCommentLike(
            @Parameter(description = "评论ID", required = true) @PathVariable Long id) {
        boolean isLiked = commentService.toggleCommentLike(id);
        return ApiResult.success(isLiked);
    }
}