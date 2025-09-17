package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.UserOnlineStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserOnlineStatusMapper extends BaseMapper<UserOnlineStatusEntity> {

    List<UserOnlineStatusEntity> selectByRoomId(@Param("roomId") Long roomId);
}