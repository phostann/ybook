package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.UserNoteInteractionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户笔记交互 Mapper 接口
 */
@Mapper
public interface UserNoteInteractionMapper extends BaseMapper<UserNoteInteractionEntity> {
    
    /**
     * 根据用户ID和笔记ID查询交互记录
     */
    UserNoteInteractionEntity selectByUserIdAndNoteId(@Param("userId") Long userId, @Param("noteId") Long noteId);
    
    /**
     * 批量查询用户对多个笔记的交互状态
     * @param userId 用户ID
     * @param noteIds 笔记ID列表
     * @return Map<noteId, UserNoteInteractionEntity>
     */
    List<UserNoteInteractionEntity> selectBatchByUserIdAndNoteIds(@Param("userId") Long userId, @Param("noteIds") List<Long> noteIds);
    
    /**
     * 分页查询用户收藏的笔记ID列表
     */
    Page<Long> selectFavoriteNoteIdsByUserId(Page<Long> page, @Param("userId") Long userId);
    
    /**
     * 分页查询用户点赞的笔记ID列表
     */
    Page<Long> selectLikedNoteIdsByUserId(Page<Long> page, @Param("userId") Long userId);
    
    /**
     * 统计笔记的点赞数
     */
    Long countLikesByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 统计笔记的收藏数
     */
    Long countFavoritesByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 批量统计多个笔记的点赞数和收藏数
     * @param noteIds 笔记ID列表
     * @return List<Map<String, Object>>，包含noteId、likeCount、favoriteCount
     */
    List<Map<String, Object>> selectInteractionCountsByNoteIds(@Param("noteIds") List<Long> noteIds);
    
    /**
     * 根据笔记ID删除所有相关的交互记录
     * @param noteId 笔记ID
     * @return 删除的记录数
     */
    int deleteByNoteId(@Param("noteId") Long noteId);
}