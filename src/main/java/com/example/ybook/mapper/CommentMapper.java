package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.CommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论 Mapper（支持多层嵌套回复和分页）
 */
@Mapper
public interface CommentMapper extends BaseMapper<CommentEntity> {
    
    /**
     * 根据笔记ID分页查询顶级评论（根评论）
     */
    Page<CommentEntity> selectRootCommentsByNoteId(
            Page<CommentEntity> page, 
            @Param("noteId") Long noteId);
    
    /**
     * 根据根评论ID分页查询所有回复（扁平化）
     */
    Page<CommentEntity> selectRepliesByRootCommentId(
            Page<CommentEntity> page, 
            @Param("rootCommentId") Long rootCommentId);
    
    /**
     * 根据用户ID分页查询评论
     */
    Page<CommentEntity> selectCommentsByUserId(
            Page<CommentEntity> page, 
            @Param("userId") Long userId);
    
    /**
     * 根据笔记ID统计根评论数量
     */
    Integer countRootCommentsByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据根评论ID统计回复数量
     */
    Integer countRepliesByRootCommentId(@Param("rootCommentId") Long rootCommentId);
    
    /**
     * 根据笔记ID统计所有评论数量（包括回复）
     */
    Integer countAllCommentsByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据笔记ID软删除所有评论
     */
    void softDeleteByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 更新根评论的回复数量
     */
    void updateRootCommentReplyCount(@Param("rootCommentId") Long rootCommentId, @Param("replyCount") Integer replyCount);
    
    /**
     * 根据评论ID获取被回复的评论信息（用于显示回复上下文）
     */
    CommentEntity selectReplyToComment(@Param("replyToCommentId") Long replyToCommentId);
}