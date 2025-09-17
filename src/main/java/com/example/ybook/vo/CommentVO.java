package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论视图对象 VO（支持多层嵌套回复）
 */
@Data
@Schema(name = "CommentVO", description = "评论视图对象")
public class CommentVO {
    
    @Schema(description = "评论ID", example = "1")
    private Long id;
    
    @Schema(description = "笔记ID", example = "1")
    private Long noteId;
    
    @Schema(description = "根评论ID", example = "1")
    private Long rootCommentId;
    
    @Schema(description = "回复的评论ID", example = "2")
    private Long replyToCommentId;
    
    @Schema(description = "评论内容", example = "这是一条很有用的笔记！")
    private String content;
    
    @Schema(description = "评论层级深度", example = "0")
    private Integer commentLevel;
    
    @Schema(description = "点赞数", example = "10")
    private Integer likeCount;
    
    @Schema(description = "回复数", example = "5")
    private Integer replyCount;
    
    @Schema(description = "IP归属地", example = "北京市")
    private String ipLocation;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "评论用户信息")
    private UserInfo user;
    
    
    @Schema(description = "是否被当前用户点赞", example = "false")
    private Boolean isLiked;
    
    @Schema(description = "被回复的用户信息")
    private UserInfo replyToUser;
    
    @Schema(description = "被回复的评论内容（用于显示上下文）", example = "原评论内容...")
    private String replyToContent;
    
    @Data
    @Schema(name = "UserInfo", description = "评论用户基本信息")
    public static class UserInfo {
        @Schema(description = "用户ID", example = "1")
        private Long id;
        
        @Schema(description = "用户名", example = "alice")
        private String username;
        
        @Schema(description = "昵称", example = "小爱同学")
        private String nickname;
        
        @Schema(description = "头像URL", example = "https://example.com/avatar.png")
        private String avatar;
    }
}