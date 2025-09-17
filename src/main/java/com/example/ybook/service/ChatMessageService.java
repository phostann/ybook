package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.MessageSendDTO;
import com.example.ybook.entity.ChatMessageEntity;
import com.example.ybook.vo.MessageVO;

public interface ChatMessageService extends IService<ChatMessageEntity> {

    /**
     * 发送消息
     * @param sendDTO 发送消息DTO
     * @param senderId 发送者ID
     * @return 发送后的消息VO
     */
    MessageVO sendMessage(MessageSendDTO sendDTO, Long senderId);

    /**
     * 分页获取聊天记录
     * @param roomId 聊天室ID
     * @param page 分页参数
     * @param userId 当前用户ID
     * @return 消息分页结果
     */
    PageResult<MessageVO> getChatHistory(Long roomId, Page<ChatMessageEntity> page, Long userId);

    /**
     * 撤回消息
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean recallMessage(Long messageId, Long userId);

    /**
     * 标记消息为已读
     * @param roomId 聊天室ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean markAsRead(Long roomId, Long userId);

    /**
     * 获取下一个序列号
     * @param roomId 聊天室ID
     * @return 序列号
     */
    Long getNextSequenceId(Long roomId);
}