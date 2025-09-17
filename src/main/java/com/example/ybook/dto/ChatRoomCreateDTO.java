package com.example.ybook.dto;

import com.example.ybook.common.ChatRoomType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "ChatRoomCreateDTO", description = "创建聊天室参数")
public class ChatRoomCreateDTO {

    @Schema(description = "聊天室名称", example = "技术交流群")
    @Size(max = 100, message = "聊天室名称长度不能超过100个字符")
    private String roomName;

    @Schema(description = "聊天室类型", example = "GROUP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "聊天室类型不能为空")
    private ChatRoomType roomType;

    @Schema(description = "聊天室头像URL", example = "https://example.com/room-avatar.png")
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String roomAvatar;

    @Schema(description = "聊天室描述", example = "这是一个技术交流群")
    private String roomDescription;

    @Schema(description = "邀请的用户ID列表（私聊时只能有一个）", example = "[2, 3, 4]")
    private List<Long> memberIds;
}