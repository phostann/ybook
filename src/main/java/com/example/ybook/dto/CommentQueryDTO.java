package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评论查询 DTO
 */
@Data
@Schema(name = "CommentQueryDTO", description = "评论查询条件数据传输对象")
public class CommentQueryDTO {
    
    @Schema(description = "笔记ID", example = "1")
    private Long noteId;
    
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    @Schema(description = "父评论ID，查询某条评论的回复时使用", example = "1")
    private Long parentId;
    
    @Schema(description = "是否只查询顶级评论", example = "true")
    private Boolean onlyTopLevel;
}