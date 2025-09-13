package com.example.ybook.dto;

import com.example.ybook.common.ImageInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 笔记更新 DTO
 */
@Data
@Schema(name = "NoteUpdateDTO", description = "笔记更新数据传输对象")
public class NoteUpdateDTO {
    
    @Schema(description = "笔记标题", example = "Spring Boot 学习笔记（已更新）")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;
    
    @Schema(description = "笔记内容", example = "## Spring Boot 基础（已更新）\n\n这是一篇关于Spring Boot的学习笔记...")
    @Size(max = 50000, message = "内容长度不能超过50000个字符")
    private String content;
    
    @Schema(description = "图片信息列表，包含URL和尺寸")
    private List<ImageInfo> images;
    
    @Schema(description = "视频", example = "video.mp4")
    @Size(max = 255, message = "视频长度不能超过255个字符")
    private String video;
    
    @Schema(description = "类型，1-图文，2-视频", example = "1")
    private String type;
    
    @Schema(description = "IP归属地", example = "北京市")
    @Size(max = 100, message = "IP归属地长度不能超过100个字符")
    private String ipLocation;
}