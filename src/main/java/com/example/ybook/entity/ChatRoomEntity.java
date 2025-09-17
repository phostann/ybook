package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ybook.common.ChatRoomType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("y_chat_room")
public class ChatRoomEntity extends BaseEntity {

    private String roomName;
    
    private ChatRoomType roomType;
    
    private String roomAvatar;
    
    private String roomDescription;
    
    private Long creatorId;
    
    private Long lastMessageId;
    
    private LocalDateTime lastMessageTime;
    
    private Integer memberCount;
    
    private String status;
}