package com.example.ybook.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.NoteCreateDTO;
import com.example.ybook.dto.NoteUpdateDTO;
import com.example.ybook.entity.NoteEntity;
import com.example.ybook.service.NoteService;
import com.example.ybook.vo.NoteVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 笔记管理接口
 */
@Tag(name = "笔记管理接口", description = "笔记的增删改查接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/notes")
public class NoteController {
    
    @Resource
    private NoteService noteService;
    
    @GetMapping
    @Operation(summary = "获取我的笔记列表", description = "获取当前用户的所有笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<List<NoteVO>> listMyNotes(HttpServletRequest request) {
        return ApiResult.success(noteService.listNotesByUserId());
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页获取我的笔记列表", description = "按页获取当前用户的笔记列表")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<PageResult<NoteVO>> pageMyNotes(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") long size,
            HttpServletRequest request) {
        Page<NoteEntity> page = new Page<>(current, size);
        return ApiResult.success(noteService.pageNotesByUserId(page));
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索笔记", description = "根据关键词搜索笔记标题和内容")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<PageResult<NoteVO>> searchNotes(
            @Parameter(description = "搜索关键词", example = "Spring Boot") @RequestParam String keyword,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") long size,
            HttpServletRequest request) {
        Page<NoteEntity> page = new Page<>(current, size);
        return ApiResult.success(noteService.searchNotes(page, keyword));
    }
    
    @GetMapping("/by-label/{labelId}")
    @Operation(summary = "根据标签获取笔记", description = "获取指定标签下的所有笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    public ApiResult<List<NoteVO>> getNotesByLabel(
            @PathVariable Long labelId,
            HttpServletRequest request) {
        return ApiResult.success(noteService.listNotesByLabelId(labelId));
    }
    
    @GetMapping("/by-label/{labelId}/page")
    @Operation(summary = "分页获取标签下的笔记", description = "按页获取指定标签下的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "标签不存在")
    })
    public ApiResult<PageResult<NoteVO>> pageNotesByLabel(
            @PathVariable Long labelId,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") long size,
            HttpServletRequest request) {
        Page<NoteEntity> page = new Page<>(current, size);
        return ApiResult.success(noteService.pageNotesByLabelId(page, labelId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取笔记详情", description = "根据笔记ID获取笔记详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "笔记不存在")
    })
    public ApiResult<NoteVO> getNoteDetail(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResult.success(noteService.getNoteById(id));
    }
    
    @PostMapping
    @Operation(summary = "创建笔记", description = "创建新的笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public ApiResult<NoteVO> createNote(
            @Valid @RequestBody NoteCreateDTO dto,
            HttpServletRequest request) {
        return ApiResult.success(noteService.createNote(dto));
    }
    
    @PatchMapping("/{id}")
    @Operation(summary = "更新笔记", description = "根据ID更新笔记信息")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "笔记不存在"),
            @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public ApiResult<NoteVO> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteUpdateDTO dto,
            HttpServletRequest request) {
        return ApiResult.success(noteService.updateNote(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除笔记", description = "根据ID删除笔记")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "笔记不存在")
    })
    public ApiResult<Boolean> deleteNote(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResult.success(noteService.deleteNote(id));
    }
    
    @PostMapping("/{id}/toggle-pin")
    @Operation(summary = "切换笔记置顶状态", description = "切换笔记的置顶状态")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "笔记不存在")
    })
    public ApiResult<NoteVO> togglePin(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResult.success(noteService.togglePin(id));
    }
}
