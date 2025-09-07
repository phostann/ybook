package com.example.ybook.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.common.ApiResult;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.LabelCreateDTO;
import com.example.ybook.dto.LabelUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.service.LabelService;
import com.example.ybook.vo.LabelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理接口
 */
@Tag(name = "标签管理接口", description = "标签的增删改查接口")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/labels")
public class LabelController {

    @Resource
    private LabelService labelService;

    @GetMapping
    @Operation(summary = "获取标签列表", description = "返回所有标签")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<List<LabelVO>> listLabels() {
        return ApiResult.success(labelService.listAll());
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取标签列表", description = "按页返回标签列表")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<PageResult<LabelVO>> pageLabels(
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") long current,
            @Parameter(description = "每页数量", example = "10") @RequestParam(defaultValue = "10") long size) {
        Page<LabelEntity> page = new Page<>(current, size);
        return ApiResult.success(labelService.pageLabels(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据标签ID获取标签")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "资源不存在")
    })
    public ApiResult<LabelVO> getLabel(@PathVariable Long id) {
        return ApiResult.success(labelService.getByLabelId(id));
    }

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新标签")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public ApiResult<LabelVO> createLabel(@Valid @RequestBody LabelCreateDTO dto) {
        return ApiResult.success(labelService.createLabel(dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "更新标签", description = "根据ID更新标签信息")
    @ApiResponses({
        @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "资源不存在"),
        @ApiResponse(responseCode = "422", description = "参数校验失败")
    })
    public ApiResult<LabelVO> updateLabel(@PathVariable Long id, @Valid @RequestBody LabelUpdateDTO dto) {
        return ApiResult.success(labelService.updateLabel(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "根据ID删除标签")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "未认证或令牌无效"),
            @ApiResponse(responseCode = "403", description = "无权限")
    })
    public ApiResult<Boolean> deleteLabel(@PathVariable Long id) {
        return ApiResult.success(labelService.deleteLabel(id));
    }
}
