package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传响应VO
 *
 * @author 柒
 * @since 2025-09-07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传响应")
public class FileUploadVO {

    @Schema(description = "文件访问URL", example = "http://localhost:9000/files/2025/09/07/uuid.jpg")
    private String url;

    @Schema(description = "原始文件名", example = "image.jpg")
    private String originalFileName;

    @Schema(description = "文件大小(字节)", example = "1024")
    private Long size;

    @Schema(description = "文件类型", example = "image/jpeg")
    private String contentType;
}