package com.example.ybook.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 图片信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ImageInfo", description = "图片信息")
public class ImageInfo {
    
    @Schema(description = "图片URL", example = "https://example.com/image.jpg")
    private String url;
    
    @Schema(description = "图片宽度（像素）", example = "1920")
    private Integer width;
    
    @Schema(description = "图片高度（像素）", example = "1080")
    private Integer height;
}