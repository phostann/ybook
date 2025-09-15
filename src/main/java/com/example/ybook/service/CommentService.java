package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.CommentCreateDTO;
import com.example.ybook.dto.CommentUpdateDTO;
import com.example.ybook.entity.CommentEntity;
import com.example.ybook.vo.CommentListVO;
import com.example.ybook.vo.CommentVO;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<CommentEntity> {
    
    /**
     * 创建评论
     */
    CommentVO createComment(CommentCreateDTO dto);
    
    /**
     * 更新评论内容
     */
    CommentVO updateComment(Long commentId, CommentUpdateDTO dto);
    
    /**
     * 删除评论（软删除）
     */
    boolean deleteComment(Long commentId);
    
    /**
     * 根据评论ID获取评论详情
     */
    CommentVO getCommentById(Long commentId);
    
    /**
     * 根据笔记ID分页获取根评论列表
     */
    PageResult<CommentVO> getRootCommentsByNoteId(Long noteId, int current, int size);
    
    /**
     * 根据根评论ID分页获取回复列表（扁平化显示）
     */
    PageResult<CommentVO> getRepliesByRootCommentId(Long rootCommentId, int current, int size);
    
    /**
     * 根据笔记ID获取评论统计信息
     */
    CommentListVO getCommentStatsByNoteId(Long noteId);
    
    /**
     * 根据用户ID分页获取用户的评论列表
     */
    PageResult<CommentVO> getCommentsByUserId(Long userId, int current, int size);
    
    /**
     * 点赞/取消点赞评论
     */
    boolean toggleCommentLike(Long commentId);
    
    /**
     * 根据笔记ID删除所有评论（软删除）
     */
    void deleteCommentsByNoteId(Long noteId);
}