package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签更新 DTO（部分字段可选）
 */
@Data
@Schema(name = "LabelUpdateDTO", description = "标签更新数据传输对象")
public class LabelUpdateDTO {
    @Schema(description = "标签名称", example = "技术")
    @Size(min = 1, max = 50, message = "标签名称长度需在1-50之间")
    private String name;
}

