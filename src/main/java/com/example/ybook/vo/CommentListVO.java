package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 评论列表视图对象 VO
 */
@Data
@Schema(name = "CommentListVO", description = "评论列表视图对象")
public class CommentListVO {
    
    @Schema(description = "评论总数", example = "100")
    private Long totalCount;
    
    @Schema(description = "顶级评论数", example = "80")
    private Long topLevelCount;
    
    @Schema(description = "评论列表")
    private List<CommentVO> comments;
}