package com.example.ybook.dto;

import com.example.ybook.common.ImageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 笔记创建 DTO
 */
@Data
@Schema(name = "NoteCreateDTO", description = "笔记创建数据传输对象")
public class NoteCreateDTO {
    
    @Schema(description = "笔记标题", example = "Spring Boot 学习笔记", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "笔记标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    @Schema(description = "笔记内容", example = "## Spring Boot 基础\n\n这是一篇关于Spring Boot的学习笔记...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "笔记内容不能为空")
    @Size(max = 50000, message = "内容长度不能超过50000个字符")
    private String content;
    
    @Schema(description = "图片信息列表，包含URL和尺寸")
    private List<ImageInfo> images;
    
    @Schema(description = "视频", example = "video.mp4")
    @Size(max = 255, message = "视频长度不能超过255个字符")
    private String video;
     
    @Schema(description = "类型，1-图文，2-视频", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "笔记类型不能为空")
    private String type;
    
    @Schema(description = "IP归属地", example = "北京市")
    @Size(max = 100, message = "IP归属地长度不能超过100个字符")
    private String ipLocation;
    
    @Schema(description = "关联的标签ID列表", example = "[1, 2, 3]")
    private List<Long> labelIds;
}