package com.example.ybook.event;

import com.example.ybook.vo.MessageVO;
import org.springframework.context.ApplicationEvent;

/**
 * 消息发送事件
 * 当消息发送成功后发布此事件，供其他组件（如WebSocket）监听处理
 */
public class MessageSentEvent extends ApplicationEvent {

    private final MessageVO messageVO;
    private final Long roomId;

    public MessageSentEvent(Object source, MessageVO messageVO, Long roomId) {
        super(source);
        this.messageVO = messageVO;
        this.roomId = roomId;
    }

    public MessageVO getMessageVO() {
        return messageVO;
    }

    public Long getRoomId() {
        return roomId;
    }
}