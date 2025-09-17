package com.example.ybook.controller;

import com.example.ybook.common.ApiResult;
import com.example.ybook.dto.ChatRoomCreateDTO;
import com.example.ybook.dto.JoinRoomDTO;
import com.example.ybook.dto.StartPrivateChatDTO;
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.service.ChatRoomService;
import com.example.ybook.vo.ChatRoomVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "聊天室管理接口", description = "聊天室的创建、加入、退出等操作")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    @Resource
    private ChatRoomService chatRoomService;

    @GetMapping
    @Operation(summary = "获取用户聊天室列表", description = "获取当前用户参与的所有聊天室")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<List<ChatRoomVO>> getUserChatRooms() {
        Long userId = CurrentUserContext.requireUserId();
        List<ChatRoomVO> rooms = chatRoomService.getUserChatRooms(userId);
        return ApiResult.success(rooms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取聊天室详情", description = "根据ID获取聊天室详细信息，包括成员列表")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限访问该聊天室"),
            @ApiResponse(responseCode = "404", description = "聊天室不存在")
    })
    public ApiResult<ChatRoomVO> getChatRoomById(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long id) {
        Long userId = CurrentUserContext.requireUserId();
        ChatRoomVO room = chatRoomService.getChatRoomById(id, userId);
        return ApiResult.success(room);
    }

    @PostMapping
    @Operation(summary = "创建聊天室", description = "创建新的聊天室（私聊或群聊）")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ApiResult<ChatRoomVO> createChatRoom(
            @Parameter(description = "聊天室创建参数", required = true) @Valid @RequestBody ChatRoomCreateDTO createDTO) {
        Long userId = CurrentUserContext.requireUserId();
        ChatRoomVO room = chatRoomService.createChatRoom(createDTO, userId);
        return ApiResult.success(room);
    }

    @PostMapping("/private")
    @Operation(summary = "开始私聊", description = "与指定用户开始私聊，如果已存在则返回现有私聊")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "目标用户不存在")
    })
    public ApiResult<ChatRoomVO> startPrivateChat(
            @Parameter(description = "开始私聊参数", required = true) @Valid @RequestBody StartPrivateChatDTO startDTO) {
        Long currentUserId = CurrentUserContext.requireUserId();
        ChatRoomVO room = chatRoomService.startPrivateChat(currentUserId, startDTO.getTargetUserId());
        return ApiResult.success(room);
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "加入聊天室", description = "用户加入指定的聊天室")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "400", description = "参数错误或已是成员"),
            @ApiResponse(responseCode = "404", description = "聊天室不存在")
    })
    public ApiResult<Boolean> joinRoom(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long id,
            @Parameter(description = "加入聊天室参数") @RequestBody(required = false) JoinRoomDTO joinRoomDTO) {
        Long userId = CurrentUserContext.requireUserId();
        if (joinRoomDTO == null) {
            joinRoomDTO = new JoinRoomDTO();
        }
        joinRoomDTO.setRoomId(id);
        boolean success = chatRoomService.joinRoom(joinRoomDTO, userId);
        return ApiResult.success(success);
    }

    @PostMapping("/{id}/leave")
    @Operation(summary = "离开聊天室", description = "用户离开指定的聊天室")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "400", description = "不是聊天室成员"),
            @ApiResponse(responseCode = "404", description = "聊天室不存在")
    })
    public ApiResult<Boolean> leaveRoom(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long id) {
        Long userId = CurrentUserContext.requireUserId();
        boolean success = chatRoomService.leaveRoom(id, userId);
        return ApiResult.success(success);
    }

    @GetMapping("/{id}/member-check")
    @Operation(summary = "检查用户是否是聊天室成员", description = "验证当前用户是否有权限访问该聊天室")
    public ApiResult<Boolean> checkMembership(
            @Parameter(description = "聊天室ID", required = true) @PathVariable Long id) {
        Long userId = CurrentUserContext.requireUserId();
        boolean isMember = chatRoomService.isRoomMember(id, userId);
        return ApiResult.success(isMember);
    }
}