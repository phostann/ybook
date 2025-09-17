package com.example.ybook.websocket;

import com.example.ybook.dto.MessageSendDTO;
import com.example.ybook.dto.WebSocketMessageDTO;
import com.example.ybook.entity.ChatRoomMemberEntity;
import com.example.ybook.event.MessageSentEvent;
import com.example.ybook.mapper.ChatRoomMemberMapper;
import com.example.ybook.service.ChatMessageService;
import com.example.ybook.service.ChatRoomService;
import com.example.ybook.service.UserOnlineStatusService;
import com.example.ybook.vo.MessageVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    // 存储用户ID到WebSocket会话的映射
    private final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    // 存储会话ID到用户ID的映射
    private final Map<String, Long> sessionUsers = new ConcurrentHashMap<>();

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserOnlineStatusService userOnlineStatusService;

    @Autowired
    private ChatRoomMemberMapper chatRoomMemberMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        String username = (String) session.getAttributes().get("username");
        
        if (userId != null) {
            // 存储会话映射
            userSessions.put(userId, session);
            sessionUsers.put(session.getId(), userId);
            
            // 更新用户在线状态
            String userAgent = session.getHandshakeHeaders().getFirst("User-Agent");
            String remoteAddress = session.getRemoteAddress() != null ? session.getRemoteAddress().getAddress().getHostAddress() : "";
            userOnlineStatusService.userOnline(userId, "WEB", remoteAddress, userAgent);
            
            // 发送连接成功消息
            WebSocketMessageDTO connectMsg = new WebSocketMessageDTO();
            connectMsg.setType("CONNECT_SUCCESS");
            connectMsg.setSenderId(userId);
            connectMsg.setTimestamp(System.currentTimeMillis());
            
            sendMessageToUser(userId, connectMsg);
            
            System.out.println("用户 " + username + " (ID: " + userId + ") 连接成功");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Long userId = sessionUsers.get(session.getId());
        if (userId == null) {
            return;
        }

        try {
            WebSocketMessageDTO wsMessage = objectMapper.readValue(message.getPayload().toString(), WebSocketMessageDTO.class);
            
            switch (wsMessage.getType()) {
                case "CHAT_MESSAGE":
                    handleChatMessage(wsMessage, userId);
                    break;
                case "JOIN_ROOM":
                    handleJoinRoom(wsMessage, userId);
                    break;
                case "LEAVE_ROOM":
                    handleLeaveRoom(wsMessage, userId);
                    break;
                case "MESSAGE_READ":
                    handleMessageRead(wsMessage, userId);
                    break;
                case "TYPING":
                    handleTyping(wsMessage, userId);
                    break;
                case "STOP_TYPING":
                    handleStopTyping(wsMessage, userId);
                    break;
                default:
                    System.out.println("未知消息类型: " + wsMessage.getType());
            }
        } catch (Exception e) {
            System.err.println("处理WebSocket消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.err.println("WebSocket传输错误: " + exception.getMessage());
        exception.printStackTrace();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        Long userId = sessionUsers.remove(session.getId());
        if (userId != null) {
            userSessions.remove(userId);
            
            // 更新用户离线状态
            userOnlineStatusService.userOffline(userId);
            
            System.out.println("用户 ID: " + userId + " 断开连接");
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void handleChatMessage(WebSocketMessageDTO wsMessage, Long senderId) {
        try {
            // 将WebSocket消息数据转换为MessageSendDTO
            Map<String, Object> data = (Map<String, Object>) wsMessage.getData();
            MessageSendDTO sendDTO = objectMapper.convertValue(data, MessageSendDTO.class);
            
            // 验证用户是否是聊天室成员
            if (!chatRoomService.isRoomMember(sendDTO.getRoomId(), senderId)) {
                return;
            }
            
            // 发送消息（Service层会自动处理广播）
            MessageVO messageVO = chatMessageService.sendMessage(sendDTO, senderId);
            
            // 注意：不需要在这里再次广播，因为Service层已经处理了广播
            
        } catch (Exception e) {
            System.err.println("处理聊天消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleJoinRoom(WebSocketMessageDTO wsMessage, Long userId) {
        Long roomId = wsMessage.getRoomId();
        if (roomId != null) {
            // 构造加入房间通知
            WebSocketMessageDTO joinMsg = new WebSocketMessageDTO();
            joinMsg.setType("USER_JOINED");
            joinMsg.setRoomId(roomId);
            joinMsg.setSenderId(userId);
            joinMsg.setTimestamp(System.currentTimeMillis());
            
            broadcastToRoom(roomId, joinMsg);
        }
    }

    private void handleLeaveRoom(WebSocketMessageDTO wsMessage, Long userId) {
        Long roomId = wsMessage.getRoomId();
        if (roomId != null) {
            // 构造离开房间通知
            WebSocketMessageDTO leaveMsg = new WebSocketMessageDTO();
            leaveMsg.setType("USER_LEFT");
            leaveMsg.setRoomId(roomId);
            leaveMsg.setSenderId(userId);
            leaveMsg.setTimestamp(System.currentTimeMillis());
            
            broadcastToRoom(roomId, leaveMsg);
        }
    }

    private void handleMessageRead(WebSocketMessageDTO wsMessage, Long userId) {
        Long roomId = wsMessage.getRoomId();
        if (roomId != null) {
            // 标记消息为已读
            chatMessageService.markAsRead(roomId, userId);
            
            // 构造已读通知
            WebSocketMessageDTO readMsg = new WebSocketMessageDTO();
            readMsg.setType("MESSAGE_READ");
            readMsg.setRoomId(roomId);
            readMsg.setSenderId(userId);
            readMsg.setTimestamp(System.currentTimeMillis());
            
            broadcastToRoom(roomId, readMsg);
        }
    }

    private void handleTyping(WebSocketMessageDTO wsMessage, Long userId) {
        Long roomId = wsMessage.getRoomId();
        if (roomId != null) {
            // 构造正在输入通知
            WebSocketMessageDTO typingMsg = new WebSocketMessageDTO();
            typingMsg.setType("USER_TYPING");
            typingMsg.setRoomId(roomId);
            typingMsg.setSenderId(userId);
            typingMsg.setTimestamp(System.currentTimeMillis());
            
            broadcastToRoomExceptSender(roomId, typingMsg, userId);
        }
    }

    private void handleStopTyping(WebSocketMessageDTO wsMessage, Long userId) {
        Long roomId = wsMessage.getRoomId();
        if (roomId != null) {
            // 构造停止输入通知
            WebSocketMessageDTO stopTypingMsg = new WebSocketMessageDTO();
            stopTypingMsg.setType("USER_STOP_TYPING");
            stopTypingMsg.setRoomId(roomId);
            stopTypingMsg.setSenderId(userId);
            stopTypingMsg.setTimestamp(System.currentTimeMillis());
            
            broadcastToRoomExceptSender(roomId, stopTypingMsg, userId);
        }
    }

    /**
     * 监听消息发送事件，进行WebSocket广播
     */
    @EventListener
    public void handleMessageSentEvent(MessageSentEvent event) {
        try {
            MessageVO messageVO = event.getMessageVO();
            Long roomId = event.getRoomId();
            
            // 构造WebSocket广播消息
            WebSocketMessageDTO broadcastMsg = new WebSocketMessageDTO();
            broadcastMsg.setType("NEW_MESSAGE");
            broadcastMsg.setRoomId(roomId);
            broadcastMsg.setSenderId(messageVO.getSender() != null ? messageVO.getSender().getId() : null);
            broadcastMsg.setData(messageVO);
            broadcastMsg.setTimestamp(System.currentTimeMillis());

            // 广播给房间所有在线成员
            broadcastToRoom(roomId, broadcastMsg);
            
            System.out.println("消息事件广播完成，房间: " + roomId);
        } catch (Exception e) {
            System.err.println("处理消息发送事件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessageToUser(Long userId, WebSocketMessageDTO message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String messageJson = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messageJson));
            } catch (Exception e) {
                System.err.println("发送消息给用户 " + userId + " 失败: " + e.getMessage());
            }
        }
    }

    public void broadcastToRoom(Long roomId, WebSocketMessageDTO message) {
        // 获取聊天室的所有活跃成员
        try {
            // 查询房间的所有活跃成员
            List<ChatRoomMemberEntity> members = chatRoomMemberMapper.selectActiveByRoomId(roomId);
            
            // 向房间内每个在线成员广播消息
            for (ChatRoomMemberEntity member : members) {
                Long userId = member.getUserId();
                WebSocketSession session = userSessions.get(userId);
                
                if (session != null && session.isOpen()) {
                    try {
                        String messageJson = objectMapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(messageJson));
                        System.out.println("消息已发送给用户 " + userId);
                    } catch (Exception e) {
                        System.err.println("发送消息给用户 " + userId + " 失败: " + e.getMessage());
                    }
                } else {
                    System.out.println("用户 " + userId + " 不在线，跳过广播");
                }
            }
        } catch (Exception e) {
            System.err.println("获取房间成员失败: " + e.getMessage());
        }
    }

    public void broadcastToRoomExceptSender(Long roomId, WebSocketMessageDTO message, Long senderId) {
        userSessions.forEach((userId, session) -> {
            if (!userId.equals(senderId) && session.isOpen()) {
                try {
                    // 验证用户是否是聊天室成员
                    if (chatRoomService.isRoomMember(roomId, userId)) {
                        String messageJson = objectMapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(messageJson));
                    }
                } catch (Exception e) {
                    System.err.println("广播消息给房间 " + roomId + " 的用户 " + userId + " 失败: " + e.getMessage());
                }
            }
        });
    }

    public boolean isUserOnline(Long userId) {
        WebSocketSession session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    public int getOnlineUserCount() {
        return (int) userSessions.values().stream()
                .filter(WebSocketSession::isOpen)
                .count();
    }
}