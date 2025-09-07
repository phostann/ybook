package com.example.ybook.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 标签更新 DTO（部分字段可选）
 */
@Data
public class LabelUpdateDTO {
    @Size(min = 1, max = 50, message = "标签名称长度需在1-50之间")
    private String name;
}

