package com.example.ybook.controller;

import com.example.ybook.common.ApiResult;
import com.example.ybook.service.FileUploadService;
import com.example.ybook.vo.FileUploadVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @author 柒
 * @since 2025-09-07
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@Tag(name = "文件上传", description = "文件上传相关接口（无需认证）")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传文件到MinIO存储（无需认证）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功",
                    content = @Content(schema = @Schema(implementation = FileUploadVO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ApiResult<FileUploadVO> uploadFile(
            @Parameter(description = "上传的文件", required = true)
            @RequestParam("file") MultipartFile file) {

        log.info("开始上传文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());

        String fileUrl = fileUploadService.uploadFile(file);

        FileUploadVO uploadVO = FileUploadVO.builder()
                .url(fileUrl)
                .originalFileName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();

        return ApiResult.success(uploadVO);
    }
}