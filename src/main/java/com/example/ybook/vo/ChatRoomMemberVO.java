package com.example.ybook.vo;

import com.example.ybook.common.ChatRoomMemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "ChatRoomMemberVO", description = "聊天室成员视图对象")
public class ChatRoomMemberVO {

    @Schema(description = "成员关系ID", example = "1")
    private Long id;

    @Schema(description = "聊天室ID", example = "1")
    private Long roomId;

    @Schema(description = "用户ID", example = "2")
    private Long userId;

    @Schema(description = "用户信息")
    private UserVO user;

    @Schema(description = "角色", example = "MEMBER")
    private ChatRoomMemberRole role;

    @Schema(description = "群昵称", example = "技术达人")
    private String nickname;

    @Schema(description = "禁言截止时间")
    private LocalDateTime muteUntil;

    @Schema(description = "加入时间")
    private LocalDateTime joinTime;

    @Schema(description = "最后阅读时间")
    private LocalDateTime lastReadTime;

    @Schema(description = "未读消息数", example = "5")
    private Integer unreadCount;

    @Schema(description = "成员状态", example = "ACTIVE")
    private String status;

    @Schema(description = "在线状态", example = "ONLINE")
    private String onlineStatus;
}