package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.ChatRoomCreateDTO;
import com.example.ybook.dto.JoinRoomDTO;
import com.example.ybook.entity.ChatRoomEntity;
import com.example.ybook.vo.ChatRoomVO;

import java.util.List;

public interface ChatRoomService extends IService<ChatRoomEntity> {

    /**
     * 创建聊天室
     * @param createDTO 创建聊天室DTO
     * @param creatorId 创建者ID
     * @return 创建后的聊天室VO
     */
    ChatRoomVO createChatRoom(ChatRoomCreateDTO createDTO, Long creatorId);

    /**
     * 开始私聊（如果不存在则创建，存在则返回）
     * @param currentUserId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 私聊房间VO
     */
    ChatRoomVO startPrivateChat(Long currentUserId, Long targetUserId);

    /**
     * 获取用户的聊天室列表
     * @param userId 用户ID
     * @return 聊天室列表
     */
    List<ChatRoomVO> getUserChatRooms(Long userId);

    /**
     * 根据ID获取聊天室详情
     * @param roomId 聊天室ID
     * @param userId 当前用户ID
     * @return 聊天室详情
     */
    ChatRoomVO getChatRoomById(Long roomId, Long userId);

    /**
     * 加入聊天室
     * @param joinRoomDTO 加入聊天室DTO
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean joinRoom(JoinRoomDTO joinRoomDTO, Long userId);

    /**
     * 离开聊天室
     * @param roomId 聊天室ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean leaveRoom(Long roomId, Long userId);

    /**
     * 检查用户是否是聊天室成员
     * @param roomId 聊天室ID
     * @param userId 用户ID
     * @return 是否为成员
     */
    boolean isRoomMember(Long roomId, Long userId);

    /**
     * 删除用户相关的聊天数据（级联删除）
     * @param userId 用户ID
     */
    void deleteUserChatData(Long userId);
}