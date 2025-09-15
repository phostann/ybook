package com.example.ybook.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户笔记交互实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("y_user_note_interaction")
public class UserNoteInteractionEntity extends BaseEntity {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 笔记ID
     */
    private Long noteId;
    
    /**
     * 交互类型位图: 1-点赞 2-收藏 4-分享(预留) 8-举报(预留)
     */
    private Integer interactionType;
    
    /**
     * 检查是否已点赞
     */
    public boolean isLiked() {
        return interactionType != null && (interactionType & 1) > 0;
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isFavorited() {
        return interactionType != null && (interactionType & 2) > 0;
    }
    
    /**
     * 检查是否已分享
     */
    public boolean isShared() {
        return interactionType != null && (interactionType & 4) > 0;
    }
    
    /**
     * 设置点赞状态
     */
    public void setLiked(boolean liked) {
        if (interactionType == null) {
            interactionType = 0;
        }
        if (liked) {
            interactionType |= 1;  // 设置点赞位
        } else {
            interactionType &= ~1; // 清除点赞位
        }
    }
    
    /**
     * 设置收藏状态
     */
    public void setFavorited(boolean favorited) {
        if (interactionType == null) {
            interactionType = 0;
        }
        if (favorited) {
            interactionType |= 2;  // 设置收藏位
        } else {
            interactionType &= ~2; // 清除收藏位
        }
    }
    
    /**
     * 设置分享状态
     */
    public void setShared(boolean shared) {
        if (interactionType == null) {
            interactionType = 0;
        }
        if (shared) {
            interactionType |= 4;  // 设置分享位
        } else {
            interactionType &= ~4; // 清除分享位
        }
    }
    
    /**
     * 检查是否有任何交互
     */
    public boolean hasAnyInteraction() {
        return interactionType != null && interactionType > 0;
    }
}