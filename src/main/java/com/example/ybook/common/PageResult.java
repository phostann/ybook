package com.example.ybook.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <p>
 * 通用分页返回结果
 * </p>
 *
 * @author 柒
 * @since 2025-09-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 注意：不要设置固定的 name，避免不同 T 的分页结果被折叠
@Schema(description = "通用分页结果")
public class PageResult<T> {
    @Schema(description = "当前页码", example = "1")
    private long current;
    @Schema(description = "每页数量", example = "10")
    private long size;
    @Schema(description = "总记录数", example = "100")
    private long total;
    @Schema(description = "总页数", example = "10")
    private long pages;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "记录列表")
    private List<T> records;
}
