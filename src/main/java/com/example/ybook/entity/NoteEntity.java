package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 笔记实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_note")
public class NoteEntity extends BaseEntity {
    
    /**
     * 用户ID，关联y_user表的id字段
     */
    private Long uid;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 图片，多个图片逗号分隔
     */
    private String images;
    
    /**
     * 视频
     */
    private String video;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 评论次数
     */
    private Integer commentCount;
    
    /**
     * 收藏次数
     */
    private Integer collectCount;
    
    /**
     * 是否置顶，0-否，1-是
     */
    private String isTop;
    
    /**
     * 类型，1-图文，2-视频
     */
    private String type;
}