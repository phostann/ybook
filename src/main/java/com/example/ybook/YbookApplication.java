package com.example.ybook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 *
 * </p>
 *
 * @author 柒
 * @since 2025-08-20 22:48:38
 */
@SpringBootApplication
@MapperScan("com.example.ybook.mapper")
public class YbookApplication {

    public static void main(String[] args) {
        SpringApplication.run(YbookApplication.class, args);
    }

}
