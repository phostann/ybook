package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.MessageStatus;
import com.example.ybook.common.PageResult;
import com.example.ybook.converter.ChatMessageConverter;
import com.example.ybook.converter.UserConverter;
import com.example.ybook.dto.MessageSendDTO;
import com.example.ybook.entity.ChatMessageEntity;
import com.example.ybook.entity.ChatRoomEntity;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.event.MessageSentEvent;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.ChatMessageMapper;
import com.example.ybook.mapper.ChatRoomMapper;
import com.example.ybook.mapper.ChatRoomMemberMapper;
import com.example.ybook.mapper.UserMapper;
import com.example.ybook.service.ChatMessageService;
import com.example.ybook.service.ChatRoomService;
import com.example.ybook.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessageEntity> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Autowired
    private ChatRoomMemberMapper chatRoomMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageConverter chatMessageConverter;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public MessageVO sendMessage(MessageSendDTO sendDTO, Long senderId) {
        // 验证聊天室存在且用户是成员
        if (!chatRoomService.isRoomMember(sendDTO.getRoomId(), senderId)) {
            throw new BizException(ApiCode.FORBIDDEN, "无权限发送消息到该聊天室");
        }

        // 获取下一个序列号
        Long sequenceId = getNextSequenceId(sendDTO.getRoomId());

        // 创建消息
        ChatMessageEntity message = chatMessageConverter.sendDTOToEntity(sendDTO, senderId, sequenceId);
        this.save(message);

        // 更新聊天室最后消息信息
        ChatRoomEntity chatRoom = chatRoomMapper.selectById(sendDTO.getRoomId());
        if (chatRoom != null) {
            chatRoom.setLastMessageId(message.getId());
            chatRoom.setLastMessageTime(LocalDateTime.now());
            chatRoomMapper.updateById(chatRoom);
        }

        // 增加其他成员的未读消息数
        chatRoomMemberMapper.incrementUnreadCount(sendDTO.getRoomId(), senderId);

        // 转换为VO并填充发送者信息
        MessageVO messageVO = chatMessageConverter.entityToVO(message);
        UserEntity sender = userMapper.selectById(senderId);
        if (sender != null) {
            messageVO.setSender(userConverter.entityToVO(sender));
        }

        // 填充回复消息信息
        if (sendDTO.getReplyToId() != null) {
            ChatMessageEntity replyToMessage = this.getById(sendDTO.getReplyToId());
            if (replyToMessage != null) {
                MessageVO replyToVO = chatMessageConverter.entityToVO(replyToMessage);
                UserEntity replyToSender = userMapper.selectById(replyToMessage.getSenderId());
                if (replyToSender != null) {
                    replyToVO.setSender(userConverter.entityToVO(replyToSender));
                }
                messageVO.setReplyToMessage(replyToVO);
            }
        }

        // 发布消息发送事件，供WebSocket等组件监听处理
        publishMessageSentEvent(messageVO, sendDTO.getRoomId());

        return messageVO;
    }

    /**
     * 发布消息发送事件
     */
    private void publishMessageSentEvent(MessageVO messageVO, Long roomId) {
        try {
            eventPublisher.publishEvent(new MessageSentEvent(this, messageVO, roomId));
            System.out.println("消息发送事件已发布到房间 " + roomId);
        } catch (Exception e) {
            System.err.println("发布消息事件失败: " + e.getMessage());
            // 事件发布失败不影响消息发送的核心逻辑
        }
    }

    @Override
    public PageResult<MessageVO> getChatHistory(Long roomId, Page<ChatMessageEntity> page, Long userId) {
        // 验证用户是聊天室成员
        if (!chatRoomService.isRoomMember(roomId, userId)) {
            throw new BizException(ApiCode.FORBIDDEN, "无权限查看该聊天室消息");
        }

        Page<ChatMessageEntity> messagePage = chatMessageMapper.selectPageByRoomId(page, roomId);

        List<MessageVO> messageVOs = messagePage.getRecords().stream()
                .map(message -> {
                    MessageVO vo = chatMessageConverter.entityToVO(message);
                    
                    // 填充发送者信息
                    UserEntity sender = userMapper.selectById(message.getSenderId());
                    if (sender != null) {
                        vo.setSender(userConverter.entityToVO(sender));
                    }
                    
                    // 填充回复消息信息
                    if (message.getReplyToId() != null) {
                        ChatMessageEntity replyToMessage = this.getById(message.getReplyToId());
                        if (replyToMessage != null) {
                            MessageVO replyToVO = chatMessageConverter.entityToVO(replyToMessage);
                            UserEntity replyToSender = userMapper.selectById(replyToMessage.getSenderId());
                            if (replyToSender != null) {
                                replyToVO.setSender(userConverter.entityToVO(replyToSender));
                            }
                            vo.setReplyToMessage(replyToVO);
                        }
                    }
                    
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(messagePage.getCurrent(), messagePage.getSize(), 
                messagePage.getTotal(), messagePage.getPages(), messageVOs);
    }

    @Override
    @Transactional
    public boolean recallMessage(Long messageId, Long userId) {
        ChatMessageEntity message = this.getById(messageId);
        if (message == null) {
            throw new BizException(ApiCode.NOT_FOUND, "消息不存在");
        }

        // 只有发送者可以撤回消息
        if (!message.getSenderId().equals(userId)) {
            throw new BizException(ApiCode.FORBIDDEN, "只能撤回自己的消息");
        }

        // 检查撤回时间限制（比如2分钟内）
        if (message.getCreateTime().isBefore(LocalDateTime.now().minusMinutes(2))) {
            throw new BizException(ApiCode.BAD_REQUEST, "超过撤回时间限制");
        }

        // 更新消息状态为已撤回
        message.setStatus(MessageStatus.RECALLED);
        message.setContent("消息已被撤回");
        return this.updateById(message);
    }

    @Override
    @Transactional
    public boolean markAsRead(Long roomId, Long userId) {
        // 验证用户是聊天室成员
        if (!chatRoomService.isRoomMember(roomId, userId)) {
            throw new BizException(ApiCode.FORBIDDEN, "无权限操作该聊天室");
        }

        // 重置未读消息数并更新最后阅读时间
        return chatRoomMemberMapper.markAsRead(roomId, userId) > 0;
    }

    @Override
    public Long getNextSequenceId(Long roomId) {
        Long maxSequenceId = chatMessageMapper.getMaxSequenceId(roomId);
        return maxSequenceId + 1;
    }
}