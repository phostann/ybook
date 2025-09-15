package com.example.ybook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 *
 * </p>
 *
 * @author 柒
 * @since 2025-08-20 22:48:38
 */
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = "com.example.ybook.mapper", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class YbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(YbookApplication.class, args);
    }

}
