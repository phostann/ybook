package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户关注统计 VO
 */
@Data
@Schema(name = "FollowStatsVO", description = "用户关注统计视图对象")
public class FollowStatsVO {
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "关注数量（我关注的人数）", example = "10")
    private Long followingCount;
    
    @Schema(description = "粉丝数量（关注我的人数）", example = "5")
    private Long followerCount;
}