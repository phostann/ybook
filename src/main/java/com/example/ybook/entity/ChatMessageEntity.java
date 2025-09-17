package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ybook.common.MessageType;
import com.example.ybook.common.MessageStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("y_chat_message")
public class ChatMessageEntity extends BaseEntity {

    private Long roomId;
    
    private Long senderId;
    
    private MessageType messageType;
    
    private String content;
    
    private String fileUrl;
    
    private String fileName;
    
    private Long fileSize;
    
    private Long replyToId;
    
    private Long sequenceId;
    
    private Integer readCount;
    
    private MessageStatus status;
}