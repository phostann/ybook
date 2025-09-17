package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "JoinRoomDTO", description = "加入聊天室参数")
public class JoinRoomDTO {

    @Schema(description = "聊天室ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "聊天室ID不能为空")
    private Long roomId;

    @Schema(description = "群昵称", example = "技术达人")
    private String nickname;
}