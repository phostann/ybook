package com.example.ybook.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签返回 VO
 */
@Data
@Schema(name = "LabelVO", description = "标签视图对象")
public class LabelVO {
    @Schema(description = "标签ID", example = "1")
    private Long id;
    @Schema(description = "标签名称", example = "科技")
    private String name;
    @Schema(description = "标签描述", example = "与科技相关的内容")
    private Integer useCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

