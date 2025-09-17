package com.example.ybook.converter;

import com.example.ybook.entity.ChatRoomMemberEntity;
import com.example.ybook.vo.ChatRoomMemberVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatRoomMemberConverter {

    /**
     * ChatRoomMemberEntity 转换为 ChatRoomMemberVO
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "onlineStatus", ignore = true)
    ChatRoomMemberVO entityToVO(ChatRoomMemberEntity entity);
}