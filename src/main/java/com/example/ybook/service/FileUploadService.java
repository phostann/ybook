package com.example.ybook.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 *
 * @author 柒
 * @since 2025-09-07
 */
public interface FileUploadService {
    
    /**
     * 上传文件到MinIO
     *
     * @param file 上传的文件
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file);
}