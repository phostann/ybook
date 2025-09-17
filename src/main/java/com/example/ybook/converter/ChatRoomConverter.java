package com.example.ybook.converter;

import com.example.ybook.dto.ChatRoomCreateDTO;
import com.example.ybook.entity.ChatRoomEntity;
import com.example.ybook.vo.ChatRoomVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatRoomConverter {

    /**
     * ChatRoomEntity 转换为 ChatRoomVO
     */
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "lastMessageContent", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "members", ignore = true)
    ChatRoomVO entityToVO(ChatRoomEntity entity);

    /**
     * ChatRoomCreateDTO 转换为 ChatRoomEntity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creatorId", source = "creatorId")
    @Mapping(target = "lastMessageId", ignore = true)
    @Mapping(target = "lastMessageTime", ignore = true)
    @Mapping(target = "memberCount", constant = "0")
    @Mapping(target = "status", constant = "ACTIVE")
    ChatRoomEntity createDTOToEntity(ChatRoomCreateDTO dto, Long creatorId);
}