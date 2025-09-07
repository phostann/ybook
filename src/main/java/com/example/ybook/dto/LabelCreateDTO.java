package com.example.ybook.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 标签创建 DTO
 */
@Data
public class LabelCreateDTO {
    @NotBlank(message = "标签名称不能为空")
    private String name;
}

