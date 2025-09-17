package com.example.ybook.vo;

import com.example.ybook.common.MessageType;
import com.example.ybook.common.MessageStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(name = "MessageVO", description = "消息视图对象")
public class MessageVO {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "聊天室ID", example = "1")
    private Long roomId;

    @Schema(description = "发送者ID", example = "2")
    private Long senderId;

    @Schema(description = "发送者信息")
    private UserVO sender;

    @Schema(description = "消息类型", example = "TEXT")
    private MessageType messageType;

    @Schema(description = "消息内容", example = "Hello, World!")
    private String content;

    @Schema(description = "文件URL", example = "https://example.com/file.jpg")
    private String fileUrl;

    @Schema(description = "文件名称", example = "image.jpg")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "1024")
    private Long fileSize;

    @Schema(description = "回复的消息ID", example = "123")
    private Long replyToId;

    @Schema(description = "回复的消息信息")
    private MessageVO replyToMessage;

    @Schema(description = "消息序列号", example = "100")
    private Long sequenceId;

    @Schema(description = "已读人数", example = "3")
    private Integer readCount;

    @Schema(description = "消息状态", example = "NORMAL")
    private MessageStatus status;

    @Schema(description = "是否已读", example = "true")
    private Boolean isRead;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}