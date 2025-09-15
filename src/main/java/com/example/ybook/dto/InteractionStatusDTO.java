package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 交互状态查询请求 DTO
 */
@Data
@Schema(name = "InteractionStatusDTO", description = "交互状态查询请求数据传输对象")
public class InteractionStatusDTO {
    
    /**
     * 笔记ID列表
     */
    @Schema(description = "笔记ID列表", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "笔记ID列表不能为空")
    private java.util.List<Long> noteIds;
}