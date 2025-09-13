package com.example.ybook.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量创建标签DTO
 */
@Data
@Schema(name = "BatchCreateLabelsDTO", description = "批量创建标签请求参数")
public class BatchCreateLabelsDTO {
    
    @Schema(description = "标签名称列表", example = "[\"健身\", \"旅游\", \"美食\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "标签名称列表不能为空")
    @Size(max = 50, message = "一次最多处理50个标签")
    private List<@Size(min = 1, max = 20, message = "标签名称长度必须在1-20个字符之间") String> names;
}