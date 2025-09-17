package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "WebSocketMessageDTO", description = "WebSocket消息传输对象")
public class WebSocketMessageDTO {

    @Schema(description = "消息类型", example = "CHAT_MESSAGE")
    private String type;

    @Schema(description = "聊天室ID", example = "1")
    private Long roomId;

    @Schema(description = "发送者ID", example = "2")
    private Long senderId;

    @Schema(description = "消息内容")
    private Object data;

    @Schema(description = "时间戳", example = "1703779200000")
    private Long timestamp;

    public enum Type {
        CHAT_MESSAGE,
        JOIN_ROOM,
        LEAVE_ROOM,
        USER_ONLINE,
        USER_OFFLINE,
        MESSAGE_READ,
        TYPING,
        STOP_TYPING
    }
}