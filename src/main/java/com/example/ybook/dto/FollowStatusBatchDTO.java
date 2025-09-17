package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量查询关注状态请求 DTO
 */
@Data
@Schema(name = "FollowStatusBatchDTO", description = "批量查询关注状态请求数据传输对象")
public class FollowStatusBatchDTO {
    
    /**
     * 用户ID列表
     */
    @Schema(description = "用户ID列表", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID列表不能为空")
    private List<Long> userIds;
}