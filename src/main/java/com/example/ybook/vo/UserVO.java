package com.example.ybook.vo;

import lombok.Data;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 用户视图对象VO (返回给前端的数据，不包含密码)
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
@Schema(name = "UserVO", description = "用户视图对象（不包含密码）")
public class UserVO {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "alice")
    private String username;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.png")
    private String avatar;
    
    @Schema(description = "昵称", example = "小爱同学")
    private String nickname;
    
    @Schema(description = "性别：0 未知；1 男；2 女", example = "1")
    private String gender; // 0: unknown; 1: 男; 2: 女
    
    @Schema(description = "邮箱", example = "alice@example.com")
    private String email;
    
    @Schema(description = "手机号", example = "13800000000")
    private String phone;
    
    @Schema(description = "状态：0 禁用；1 正常", example = "1")
    private String status; // 0: disabled; 1: normal
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "关注数量（该用户关注的人数）", example = "10")
    private Long followingCount;
    
    @Schema(description = "粉丝数量（关注该用户的人数）", example = "5")
    private Long followerCount;
    
    @Schema(description = "是否已关注该用户", example = "true")
    private Boolean isFollowing;
}
