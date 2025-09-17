package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.ChatMessageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessageEntity> {

    Long getMaxSequenceId(@Param("roomId") Long roomId);

    Page<ChatMessageEntity> selectPageByRoomId(Page<ChatMessageEntity> page, @Param("roomId") Long roomId);

    int incrementReadCount(@Param("messageId") Long messageId);
}