package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("y_user_online_status")
public class UserOnlineStatusEntity extends BaseEntity {

    private Long userId;
    
    private String status;
    
    private LocalDateTime lastActiveTime;
    
    private String deviceType;
    
    private String ipAddress;
    
    private String userAgent;
}