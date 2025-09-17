package com.example.ybook.dto;

import com.example.ybook.common.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "MessageSendDTO", description = "发送消息参数")
public class MessageSendDTO {

    @Schema(description = "聊天室ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "聊天室ID不能为空")
    private Long roomId;

    @Schema(description = "消息类型", example = "TEXT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "消息类型不能为空")
    private MessageType messageType;

    @Schema(description = "消息内容", example = "Hello, World!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @Schema(description = "文件URL（当消息类型为文件时）", example = "https://example.com/file.jpg")
    private String fileUrl;

    @Schema(description = "文件名称", example = "image.jpg")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "1024")
    private Long fileSize;

    @Schema(description = "回复的消息ID", example = "123")
    private Long replyToId;
}