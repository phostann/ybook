package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.ChatRoomMemberEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMemberMapper extends BaseMapper<ChatRoomMemberEntity> {

    List<ChatRoomMemberEntity> selectActiveByRoomId(@Param("roomId") Long roomId);

    ChatRoomMemberEntity selectByRoomIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    int incrementUnreadCount(@Param("roomId") Long roomId, @Param("senderId") Long senderId);

    int markAsRead(@Param("roomId") Long roomId, @Param("userId") Long userId);
}