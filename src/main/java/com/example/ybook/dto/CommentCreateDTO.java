package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论创建 DTO（支持多层嵌套回复）
 */
@Data
@Schema(name = "CommentCreateDTO", description = "评论创建数据传输对象")
public class CommentCreateDTO {
    
    @Schema(description = "笔记ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "笔记ID不能为空")
    private Long noteId;
    
    @Schema(description = "根评论ID，回复时必填", example = "1")
    private Long rootCommentId;
    
    @Schema(description = "回复的评论ID，回复时必填", example = "2")
    private Long replyToCommentId;
    
    @Schema(description = "评论内容", example = "这是一条很有用的笔记！", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;
    
    @Schema(description = "IP归属地", example = "北京市")
    @Size(max = 100, message = "IP归属地不能超过100个字符")
    private String ipLocation;
}