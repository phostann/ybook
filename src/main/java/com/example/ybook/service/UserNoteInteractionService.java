package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.vo.InteractionStatusVO;
import com.example.ybook.vo.NoteVO;

import java.util.List;

/**
 * 用户笔记交互服务接口
 */
public interface UserNoteInteractionService {
    
    /**
     * 切换点赞状态
     * @param noteId 笔记ID
     * @return 是否点赞成功（true-已点赞，false-已取消点赞）
     */
    boolean toggleLike(Long noteId);
    
    /**
     * 切换收藏状态
     * @param noteId 笔记ID 
     * @return 是否收藏成功（true-已收藏，false-已取消收藏）
     */
    boolean toggleFavorite(Long noteId);
    
    /**
     * 获取用户对单个笔记的交互状态
     * @param noteId 笔记ID
     * @param userId 用户ID，如果为null则使用当前登录用户
     * @return 交互状态
     */
    InteractionStatusVO.InteractionDetailVO getInteractionStatus(Long noteId, Long userId);
    
    /**
     * 批量获取用户对多个笔记的交互状态
     * @param noteIds 笔记ID列表
     * @param userId 用户ID，如果为null则使用当前登录用户
     * @return 交互状态映射
     */
    InteractionStatusVO batchGetInteractionStatus(List<Long> noteIds, Long userId);
    
    /**
     * 获取用户收藏的笔记列表（分页）
     * @param userId 用户ID，如果为null则使用当前登录用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 收藏的笔记列表
     */
    Page<NoteVO> getUserFavoriteNotes(Long userId, int pageNum, int pageSize);
    
    /**
     * 获取用户点赞的笔记列表（分页）
     * @param userId 用户ID，如果为null则使用当前登录用户
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 点赞的笔记列表
     */
    Page<NoteVO> getUserLikedNotes(Long userId, int pageNum, int pageSize);
    
    /**
     * 异步更新笔记的点赞计数
     * @param noteId 笔记ID
     */
    void updateLikeCountAsync(Long noteId);
    
    /**
     * 异步更新笔记的收藏计数
     * @param noteId 笔记ID
     */
    void updateFavoriteCountAsync(Long noteId);
}