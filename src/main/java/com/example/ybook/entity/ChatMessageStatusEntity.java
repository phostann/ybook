package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("y_chat_message_status")
public class ChatMessageStatusEntity extends BaseEntity {

    private Long messageId;
    
    private Long userId;
    
    private String status;
    
    private LocalDateTime statusTime;
}