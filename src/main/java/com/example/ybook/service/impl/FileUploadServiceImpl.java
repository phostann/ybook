package com.example.ybook.service.impl;

import com.example.ybook.common.ApiCode;
import com.example.ybook.config.MinioConfig;
import com.example.ybook.exception.BizException;
import com.example.ybook.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传服务实现类
 *
 * @author 柒
 * @since 2025-09-07
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public FileUploadServiceImpl(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                throw new BizException(ApiCode.BAD_REQUEST, "文件不能为空");
            }

            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new BizException(ApiCode.BAD_REQUEST, "文件名不能为空");
            }

            String extension = getFileExtension(originalFilename);
            
            // 生成唯一文件名 (按日期分目录)
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = dateFolder + "/" + UUID.randomUUID() + extension;

            // 上传文件到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回可访问的URL
            String fileUrl = minioConfig.getEndpoint() + "/" + minioConfig.getBucketName() + "/" + fileName;
            
            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BizException(ApiCode.INTERNAL_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }
}