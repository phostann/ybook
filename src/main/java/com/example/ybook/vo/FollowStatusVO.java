package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关注状态 VO
 */
@Data
@Schema(name = "FollowStatusVO", description = "关注状态视图对象")
public class FollowStatusVO {
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "是否已关注", example = "true")
    private Boolean isFollowing;
}