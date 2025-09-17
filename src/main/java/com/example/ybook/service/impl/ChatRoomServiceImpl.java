package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.ChatRoomMemberRole;
import com.example.ybook.common.ChatRoomType;
import com.example.ybook.converter.ChatRoomConverter;
import com.example.ybook.converter.ChatRoomMemberConverter;
import com.example.ybook.converter.UserConverter;
import com.example.ybook.dto.ChatRoomCreateDTO;
import com.example.ybook.dto.JoinRoomDTO;
import com.example.ybook.entity.ChatRoomEntity;
import com.example.ybook.entity.ChatRoomMemberEntity;
import com.example.ybook.entity.ChatMessageEntity;
import com.example.ybook.entity.ChatMessageStatusEntity;
import com.example.ybook.entity.UserOnlineStatusEntity;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.ChatRoomMapper;
import com.example.ybook.mapper.ChatRoomMemberMapper;
import com.example.ybook.mapper.ChatMessageMapper;
import com.example.ybook.mapper.ChatMessageStatusMapper;
import com.example.ybook.mapper.UserOnlineStatusMapper;
import com.example.ybook.mapper.UserMapper;
import com.example.ybook.service.ChatRoomService;
import com.example.ybook.vo.ChatRoomMemberVO;
import com.example.ybook.vo.ChatRoomVO;
import com.example.ybook.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomServiceImpl extends ServiceImpl<ChatRoomMapper, ChatRoomEntity> implements ChatRoomService {

    @Autowired
    private ChatRoomMapper chatRoomMapper;

    @Autowired
    private ChatRoomMemberMapper chatRoomMemberMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private ChatMessageStatusMapper chatMessageStatusMapper;

    @Autowired
    private UserOnlineStatusMapper userOnlineStatusMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatRoomConverter chatRoomConverter;

    @Autowired
    private ChatRoomMemberConverter chatRoomMemberConverter;

    @Autowired
    private UserConverter userConverter;

    @Override
    @Transactional
    public ChatRoomVO createChatRoom(ChatRoomCreateDTO createDTO, Long creatorId) {
        // 验证创建者
        UserEntity creator = userMapper.selectById(creatorId);
        if (creator == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }

        // 私聊验证
        if (createDTO.getRoomType() == ChatRoomType.PRIVATE) {
            if (createDTO.getMemberIds() == null || createDTO.getMemberIds().size() != 1) {
                throw new BizException(ApiCode.BAD_REQUEST, "私聊只能邀请一个用户");
            }
            
            Long targetUserId = createDTO.getMemberIds().get(0);
            // 检查是否已存在私聊
            ChatRoomEntity existingRoom = checkExistingPrivateChat(creatorId, targetUserId);
            if (existingRoom != null) {
                return getChatRoomById(existingRoom.getId(), creatorId);
            }
        }

        // 创建聊天室
        ChatRoomEntity chatRoom = chatRoomConverter.createDTOToEntity(createDTO, creatorId);
        if (createDTO.getRoomType() == ChatRoomType.PRIVATE) {
            // 私聊不需要名称和描述
            chatRoom.setRoomName(null);
            chatRoom.setRoomDescription(null);
        }
        
        this.save(chatRoom);

        // 添加创建者为群主
        ChatRoomMemberEntity creatorMember = new ChatRoomMemberEntity();
        creatorMember.setRoomId(chatRoom.getId());
        creatorMember.setUserId(creatorId);
        creatorMember.setRole(ChatRoomMemberRole.OWNER);
        creatorMember.setJoinTime(LocalDateTime.now());
        creatorMember.setStatus("ACTIVE");
        chatRoomMemberMapper.insert(creatorMember);

        int memberCount = 1;

        // 添加邀请的成员
        if (createDTO.getMemberIds() != null) {
            for (Long memberId : createDTO.getMemberIds()) {
                if (!memberId.equals(creatorId)) {
                    UserEntity memberUser = userMapper.selectById(memberId);
                    if (memberUser != null) {
                        ChatRoomMemberEntity member = new ChatRoomMemberEntity();
                        member.setRoomId(chatRoom.getId());
                        member.setUserId(memberId);
                        member.setRole(ChatRoomMemberRole.MEMBER);
                        member.setJoinTime(LocalDateTime.now());
                        member.setStatus("ACTIVE");
                        chatRoomMemberMapper.insert(member);
                        memberCount++;
                    }
                }
            }
        }

        // 更新成员数量
        chatRoom.setMemberCount(memberCount);
        this.updateById(chatRoom);

        return getChatRoomById(chatRoom.getId(), creatorId);
    }

    @Override
    @Transactional
    public ChatRoomVO startPrivateChat(Long currentUserId, Long targetUserId) {
        // 验证目标用户存在
        UserEntity targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new BizException(ApiCode.USER_NOT_FOUND);
        }

        // 不能和自己私聊
        if (currentUserId.equals(targetUserId)) {
            throw new BizException(ApiCode.BAD_REQUEST, "不能和自己私聊");
        }

        // 检查是否已存在私聊
        ChatRoomEntity existingRoom = findExistingPrivateChat(currentUserId, targetUserId);
        if (existingRoom != null) {
            return getChatRoomById(existingRoom.getId(), currentUserId);
        }

        // 创建新的私聊
        ChatRoomEntity chatRoom = new ChatRoomEntity();
        chatRoom.setRoomType(ChatRoomType.PRIVATE);
        chatRoom.setCreatorId(currentUserId);
        chatRoom.setMemberCount(2);
        chatRoom.setStatus("ACTIVE");
        this.save(chatRoom);

        // 添加两个用户为成员
        // 当前用户
        ChatRoomMemberEntity currentMember = new ChatRoomMemberEntity();
        currentMember.setRoomId(chatRoom.getId());
        currentMember.setUserId(currentUserId);
        currentMember.setRole(ChatRoomMemberRole.OWNER);
        currentMember.setJoinTime(LocalDateTime.now());
        currentMember.setStatus("ACTIVE");
        chatRoomMemberMapper.insert(currentMember);

        // 目标用户
        ChatRoomMemberEntity targetMember = new ChatRoomMemberEntity();
        targetMember.setRoomId(chatRoom.getId());
        targetMember.setUserId(targetUserId);
        targetMember.setRole(ChatRoomMemberRole.MEMBER);
        targetMember.setJoinTime(LocalDateTime.now());
        targetMember.setStatus("ACTIVE");
        chatRoomMemberMapper.insert(targetMember);

        return getChatRoomById(chatRoom.getId(), currentUserId);
    }

    @Override
    public List<ChatRoomVO> getUserChatRooms(Long userId) {
        List<ChatRoomEntity> rooms = chatRoomMapper.selectRoomsByUserId(userId);
        return rooms.stream()
                .map(room -> {
                    ChatRoomVO vo = chatRoomConverter.entityToVO(room);
                    
                    // 为私聊设置显示名称
                    if (room.getRoomType() == ChatRoomType.PRIVATE) {
                        // 找到对方用户
                        List<ChatRoomMemberEntity> members = chatRoomMemberMapper.selectActiveByRoomId(room.getId());
                        Long otherUserId = members.stream()
                                .map(ChatRoomMemberEntity::getUserId)
                                .filter(id -> !id.equals(userId))
                                .findFirst()
                                .orElse(null);
                        
                        if (otherUserId != null) {
                            UserEntity otherUser = userMapper.selectById(otherUserId);
                            if (otherUser != null) {
                                vo.setRoomName(otherUser.getNickname() != null ? otherUser.getNickname() : otherUser.getUsername());
                                vo.setRoomAvatar(otherUser.getAvatar());
                            }
                        }
                    }
                    
                    // 获取未读消息数
                    ChatRoomMemberEntity member = chatRoomMemberMapper.selectByRoomIdAndUserId(room.getId(), userId);
                    if (member != null) {
                        vo.setUnreadCount(member.getUnreadCount());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ChatRoomVO getChatRoomById(Long roomId, Long userId) {
        ChatRoomEntity room = this.getById(roomId);
        if (room == null) {
            throw new BizException(ApiCode.NOT_FOUND, "聊天室不存在");
        }

        // 检查用户是否是成员
        if (!isRoomMember(roomId, userId)) {
            throw new BizException(ApiCode.FORBIDDEN, "无权限访问该聊天室");
        }

        ChatRoomVO vo = chatRoomConverter.entityToVO(room);

        // 设置创建者信息
        UserEntity creator = userMapper.selectById(room.getCreatorId());
        if (creator != null) {
            vo.setCreator(userConverter.entityToVO(creator));
        }

        // 获取成员列表
        List<ChatRoomMemberEntity> members = chatRoomMemberMapper.selectActiveByRoomId(roomId);
        List<ChatRoomMemberVO> memberVOs = members.stream()
                .map(member -> {
                    ChatRoomMemberVO memberVO = chatRoomMemberConverter.entityToVO(member);
                    UserEntity user = userMapper.selectById(member.getUserId());
                    if (user != null) {
                        memberVO.setUser(userConverter.entityToVO(user));
                    }
                    return memberVO;
                })
                .collect(Collectors.toList());
        vo.setMembers(memberVOs);

        // 获取当前用户未读消息数
        ChatRoomMemberEntity currentMember = chatRoomMemberMapper.selectByRoomIdAndUserId(roomId, userId);
        if (currentMember != null) {
            vo.setUnreadCount(currentMember.getUnreadCount());
        }

        return vo;
    }

    @Override
    @Transactional
    public boolean joinRoom(JoinRoomDTO joinRoomDTO, Long userId) {
        // 检查聊天室是否存在
        ChatRoomEntity room = this.getById(joinRoomDTO.getRoomId());
        if (room == null) {
            throw new BizException(ApiCode.NOT_FOUND, "聊天室不存在");
        }

        // 私聊不允许主动加入
        if (room.getRoomType() == ChatRoomType.PRIVATE) {
            throw new BizException(ApiCode.BAD_REQUEST, "私聊无法主动加入");
        }

        // 检查是否已经是成员
        ChatRoomMemberEntity existingMember = chatRoomMemberMapper.selectByRoomIdAndUserId(joinRoomDTO.getRoomId(), userId);
        if (existingMember != null && "ACTIVE".equals(existingMember.getStatus())) {
            throw new BizException(ApiCode.BAD_REQUEST, "已经是聊天室成员");
        }

        // 创建成员记录
        ChatRoomMemberEntity member = new ChatRoomMemberEntity();
        member.setRoomId(joinRoomDTO.getRoomId());
        member.setUserId(userId);
        member.setRole(ChatRoomMemberRole.MEMBER);
        member.setNickname(joinRoomDTO.getNickname());
        member.setJoinTime(LocalDateTime.now());
        member.setStatus("ACTIVE");

        if (existingMember != null) {
            // 更新已存在的记录
            member.setId(existingMember.getId());
            chatRoomMemberMapper.updateById(member);
        } else {
            // 插入新记录
            chatRoomMemberMapper.insert(member);
            // 更新聊天室成员数量
            room.setMemberCount(room.getMemberCount() + 1);
            this.updateById(room);
        }

        return true;
    }

    @Override
    @Transactional
    public boolean leaveRoom(Long roomId, Long userId) {
        ChatRoomMemberEntity member = chatRoomMemberMapper.selectByRoomIdAndUserId(roomId, userId);
        if (member == null || !"ACTIVE".equals(member.getStatus())) {
            throw new BizException(ApiCode.BAD_REQUEST, "不是聊天室成员");
        }

        // 更新成员状态为离开
        member.setStatus("LEFT");
        chatRoomMemberMapper.updateById(member);

        // 更新聊天室成员数量
        ChatRoomEntity room = this.getById(roomId);
        if (room != null) {
            room.setMemberCount(Math.max(0, room.getMemberCount() - 1));
            this.updateById(room);
        }

        return true;
    }

    @Override
    public boolean isRoomMember(Long roomId, Long userId) {
        ChatRoomMemberEntity member = chatRoomMemberMapper.selectByRoomIdAndUserId(roomId, userId);
        return member != null && "ACTIVE".equals(member.getStatus());
    }

    @Override
    @Transactional
    public void deleteUserChatData(Long userId) {
        // 1. 删除用户在线状态
        LambdaQueryWrapper<UserOnlineStatusEntity> onlineWrapper = new LambdaQueryWrapper<>();
        onlineWrapper.eq(UserOnlineStatusEntity::getUserId, userId);
        userOnlineStatusMapper.delete(onlineWrapper);

        // 2. 获取用户参与的聊天室
        List<ChatRoomEntity> userRooms = chatRoomMapper.selectRoomsByUserId(userId);
        
        for (ChatRoomEntity room : userRooms) {
            // 3. 删除用户的消息状态
            LambdaQueryWrapper<ChatMessageStatusEntity> statusWrapper = new LambdaQueryWrapper<>();
            statusWrapper.eq(ChatMessageStatusEntity::getUserId, userId);
            chatMessageStatusMapper.delete(statusWrapper);

            // 4. 删除用户发送的消息
            LambdaQueryWrapper<ChatMessageEntity> messageWrapper = new LambdaQueryWrapper<>();
            messageWrapper.eq(ChatMessageEntity::getSenderId, userId);
            messageWrapper.eq(ChatMessageEntity::getRoomId, room.getId());
            chatMessageMapper.delete(messageWrapper);

            // 5. 删除聊天室成员记录
            LambdaQueryWrapper<ChatRoomMemberEntity> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(ChatRoomMemberEntity::getUserId, userId);
            memberWrapper.eq(ChatRoomMemberEntity::getRoomId, room.getId());
            chatRoomMemberMapper.delete(memberWrapper);

            // 6. 处理私聊房间：如果是私聊且用户是创建者，删除整个聊天室
            if (room.getRoomType() == ChatRoomType.PRIVATE && room.getCreatorId().equals(userId)) {
                // 删除私聊中的所有消息
                LambdaQueryWrapper<ChatMessageEntity> roomMessageWrapper = new LambdaQueryWrapper<>();
                roomMessageWrapper.eq(ChatMessageEntity::getRoomId, room.getId());
                chatMessageMapper.delete(roomMessageWrapper);

                // 删除所有成员
                LambdaQueryWrapper<ChatRoomMemberEntity> roomMemberWrapper = new LambdaQueryWrapper<>();
                roomMemberWrapper.eq(ChatRoomMemberEntity::getRoomId, room.getId());
                chatRoomMemberMapper.delete(roomMemberWrapper);

                // 删除聊天室
                this.removeById(room.getId());
            } else if (room.getRoomType() == ChatRoomType.GROUP) {
                // 群聊：更新成员数量
                int currentMemberCount = Math.max(0, room.getMemberCount() - 1);
                room.setMemberCount(currentMemberCount);
                this.updateById(room);
            }
        }
    }

    private ChatRoomEntity findExistingPrivateChat(Long user1Id, Long user2Id) {
        return checkExistingPrivateChat(user1Id, user2Id);
    }

    private ChatRoomEntity checkExistingPrivateChat(Long user1Id, Long user2Id) {
        // 查找两个用户之间是否已存在私聊
        LambdaQueryWrapper<ChatRoomEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatRoomEntity::getRoomType, ChatRoomType.PRIVATE);
        
        List<ChatRoomEntity> privateRooms = this.list(wrapper);
        
        for (ChatRoomEntity room : privateRooms) {
            List<ChatRoomMemberEntity> members = chatRoomMemberMapper.selectActiveByRoomId(room.getId());
            if (members.size() == 2) {
                List<Long> memberIds = members.stream()
                        .map(ChatRoomMemberEntity::getUserId)
                        .collect(Collectors.toList());
                if (memberIds.contains(user1Id) && memberIds.contains(user2Id)) {
                    return room;
                }
            }
        }
        return null;
    }
}