package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.MessageSendDTO;
import com.example.ybook.entity.ChatMessageEntity;
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.service.ChatMessageService;
import com.example.ybook.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "聊天消息接口", description = "聊天消息的发送、查询、撤回等操作")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessageController {

    @Resource
    private ChatMessageService chatMessageService;

    @PostMapping
    @Operation(summary = "发送消息", description = "发送聊天消息到指定聊天室")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限发送消息到该聊天室"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ApiResult<MessageVO> sendMessage(
            @Parameter(description = "消息发送参数", required = true) @Valid @RequestBody MessageSendDTO sendDTO) {
        Long userId = CurrentUserContext.requireUserId();
        MessageVO message = chatMessageService.sendMessage(sendDTO, userId);
        return ApiResult.success(message);
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "获取聊天记录", description = "分页获取指定聊天室的聊天记录")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限查看该聊天室消息"),
            @ApiResponse(responseCode = "404", description = "聊天室不存在")
    })
    public ApiResult<PageResult<MessageVO>> getChatHistory(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long roomId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        Long userId = CurrentUserContext.requireUserId();
        Page<ChatMessageEntity> pageParam = new Page<>(page, size);
        PageResult<MessageVO> messages = chatMessageService.getChatHistory(roomId, pageParam, userId);
        return ApiResult.success(messages);
    }

    @PostMapping("/{id}/recall")
    @Operation(summary = "撤回消息", description = "撤回指定的消息（仅限发送者在时间限制内）")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "只能撤回自己的消息"),
            @ApiResponse(responseCode = "400", description = "消息不存在或超过撤回时间限制"),
            @ApiResponse(responseCode = "404", description = "消息不存在")
    })
    public ApiResult<Boolean> recallMessage(
            @Parameter(description = "消息ID", required = true) @PathVariable Long id) {
        Long userId = CurrentUserContext.requireUserId();
        boolean success = chatMessageService.recallMessage(id, userId);
        return ApiResult.success(success);
    }

    @PostMapping("/room/{roomId}/read")
    @Operation(summary = "标记消息为已读", description = "将指定聊天室的消息标记为已读")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限操作该聊天室"),
            @ApiResponse(responseCode = "404", description = "聊天室不存在")
    })
    public ApiResult<Boolean> markAsRead(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long roomId) {
        Long userId = CurrentUserContext.requireUserId();
        boolean success = chatMessageService.markAsRead(roomId, userId);
        return ApiResult.success(success);
    }
}