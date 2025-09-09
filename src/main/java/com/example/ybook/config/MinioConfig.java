package com.example.ybook.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO配置类
 *
 * @author 柒
 * @since 2025-09-07
 */
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint = "http://localhost:9000";
    private String accessKey = "VpvLTWcT0Ozq0poYDKNg";
    private String secretKey = "bO8K1dM2kJv9D3Ok133ZXLHhvh6KD4oGAlEIWKTV";
    private String bucketName = "files";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}