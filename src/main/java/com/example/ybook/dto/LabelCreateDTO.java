package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签创建 DTO
 */
@Data
@Schema(name = "LabelCreateDTO", description = "标签创建数据传输对象")
public class LabelCreateDTO {
    @Schema(description = "标签名称", example = "科技", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签名称不能为空")
    private String name;
}

