package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "StartPrivateChatDTO", description = "开始私聊参数")
public class StartPrivateChatDTO {

    @Schema(description = "目标用户ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标用户ID不能为空")
    private Long targetUserId;
}