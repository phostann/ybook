package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.ChatRoomEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatRoomMapper extends BaseMapper<ChatRoomEntity> {

    List<ChatRoomEntity> selectRoomsByUserId(@Param("userId") Long userId);
}