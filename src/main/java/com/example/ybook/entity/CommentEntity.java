package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体（支持多层嵌套回复）
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_comment")
public class CommentEntity extends BaseEntity {
    
    /**
     * 笔记ID
     */
    private Long noteId;
    
    /**
     * 评论用户ID
     */
    private Long userId;
    
    /**
     * 根评论ID，顶级评论为NULL
     */
    private Long rootCommentId;
    
    /**
     * 回复的评论ID，顶级评论为NULL
     */
    private Long replyToCommentId;
    
    /**
     * 评论内容
     */
    private String content;
    
    /**
     * 评论层级深度：0-顶级，1,2,3...-各级回复
     */
    private Integer commentLevel;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 回复数
     */
    private Integer replyCount;
    
    /**
     * 是否删除，0-否，1-是
     */
    private Integer isDeleted;
    
    /**
     * IP归属地
     */
    private String ipLocation;
}