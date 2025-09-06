package com.example.ybook.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户视图对象VO (返回给前端的数据，不包含密码)
 * </p>
 *
 * @author 柒
 * @since 2025-09-05
 */
@Data
public class UserVO {
    
    private Long id;
    
    private String username;
    
    private String avatar;
    
    private String gender; // 0: unknown; 1: 男; 2: 女
    
    private String email;
    
    private String phone;
    
    private String status; // 0: disabled; 1: normal
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}