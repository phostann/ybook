package com.example.ybook.converter;

import com.example.ybook.dto.MessageSendDTO;
import com.example.ybook.entity.ChatMessageEntity;
import com.example.ybook.vo.MessageVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageConverter {

    /**
     * ChatMessageEntity 转换为 MessageVO
     */
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "replyToMessage", ignore = true)
    @Mapping(target = "isRead", ignore = true)
    MessageVO entityToVO(ChatMessageEntity entity);

    /**
     * MessageSendDTO 转换为 ChatMessageEntity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "senderId", source = "senderId")
    @Mapping(target = "sequenceId", source = "sequenceId")
    @Mapping(target = "readCount", constant = "0")
    @Mapping(target = "status", constant = "NORMAL")
    ChatMessageEntity sendDTOToEntity(MessageSendDTO dto, Long senderId, Long sequenceId);
}