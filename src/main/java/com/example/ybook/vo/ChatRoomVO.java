package com.example.ybook.vo;

import com.example.ybook.common.ChatRoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "ChatRoomVO", description = "聊天室视图对象")
public class ChatRoomVO {

    @Schema(description = "聊天室ID", example = "1")
    private Long id;

    @Schema(description = "聊天室名称", example = "技术交流群")
    private String roomName;

    @Schema(description = "聊天室类型", example = "GROUP")
    private ChatRoomType roomType;

    @Schema(description = "聊天室头像URL", example = "https://example.com/room-avatar.png")
    private String roomAvatar;

    @Schema(description = "聊天室描述", example = "这是一个技术交流群")
    private String roomDescription;

    @Schema(description = "创建者ID", example = "1")
    private Long creatorId;

    @Schema(description = "创建者信息")
    private UserVO creator;

    @Schema(description = "最后一条消息ID", example = "123")
    private Long lastMessageId;

    @Schema(description = "最后消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "最后消息内容", example = "Hello, World!")
    private String lastMessageContent;

    @Schema(description = "成员数量", example = "5")
    private Integer memberCount;

    @Schema(description = "聊天室状态", example = "ACTIVE")
    private String status;

    @Schema(description = "未读消息数", example = "3")
    private Integer unreadCount;

    @Schema(description = "成员列表")
    private List<ChatRoomMemberVO> members;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}