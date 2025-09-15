package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 评论更新 DTO
 */
@Data
@Schema(name = "CommentUpdateDTO", description = "评论更新数据传输对象")
public class CommentUpdateDTO {
    
    @Schema(description = "评论内容", example = "这是一条更新后的评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;
}