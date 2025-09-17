package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.ybook.common.ChatRoomMemberRole;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("y_chat_room_member")
public class ChatRoomMemberEntity extends BaseEntity {

    private Long roomId;
    
    private Long userId;
    
    private ChatRoomMemberRole role;
    
    private String nickname;
    
    private LocalDateTime muteUntil;
    
    private LocalDateTime joinTime;
    
    private LocalDateTime lastReadTime;
    
    private Integer unreadCount;
    
    private String status;
}