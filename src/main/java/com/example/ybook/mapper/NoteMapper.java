package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.NoteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔记 Mapper
 */
@Mapper
public interface NoteMapper extends BaseMapper<NoteEntity> {
    
    /**
     * 根据用户ID查询笔记列表
     */
    List<NoteEntity> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID分页查询笔记
     */
    Page<NoteEntity> selectPageByUserId(Page<NoteEntity> page, @Param("userId") Long userId);
    
    /**
     * 根据标签ID查询笔记列表
     */
    List<NoteEntity> selectByLabelId(@Param("labelId") Long labelId, @Param("userId") Long userId);
    
    /**
     * 根据标签ID分页查询笔记
     */
    Page<NoteEntity> selectPageByLabelId(Page<NoteEntity> page, @Param("labelId") Long labelId, @Param("userId") Long userId);
    
    /**
     * 根据关键词搜索笔记（标题和内容）
     */
    Page<NoteEntity> searchNotes(Page<NoteEntity> page, @Param("keyword") String keyword, @Param("userId") Long userId);
}