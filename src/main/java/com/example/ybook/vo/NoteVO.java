package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔记返回 VO
 */
@Data
@Schema(name = "NoteVO", description = "笔记视图对象")
public class NoteVO {
    
    @Schema(description = "笔记ID", example = "1")
    private Long id;

    @Schema(description = "笔记所属用户ID", example = "1001")
    private Long uid;
    
    @Schema(description = "笔记标题", example = "Spring Boot 学习笔记")
    private String title;
    
    @Schema(description = "笔记内容", example = "## Spring Boot 基础\n\n这是一篇关于Spring Boot的学习笔记...")
    private String content;
    
    @Schema(description = "图片，多个图片逗号分隔", example = "image1.jpg,image2.jpg")
    private String images;
    
    @Schema(description = "视频", example = "video.mp4")
    private String video;
    
    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount;
    
    @Schema(description = "点赞次数", example = "50")
    private Integer likeCount;
    
    @Schema(description = "评论次数", example = "20")
    private Integer commentCount;
    
    @Schema(description = "收藏次数", example = "30")
    private Integer collectCount;
    
    @Schema(description = "是否置顶，0-否，1-是", example = "0")
    private String isTop;
    
    @Schema(description = "类型，1-图文，2-视频", example = "1")
    private String type;
    
    @Schema(description = "关联的标签列表")
    private List<LabelVO> labels;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}