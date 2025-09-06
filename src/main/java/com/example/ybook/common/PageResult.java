package com.example.ybook.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class PageResult<T> {
    private long current;
    private long size;
    private long total;
    private long pages;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<T> records;
}
