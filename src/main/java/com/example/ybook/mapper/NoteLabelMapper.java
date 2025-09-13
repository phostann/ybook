package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.entity.NoteLabelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 笔记标签关联 Mapper
 */
@Mapper
public interface NoteLabelMapper extends BaseMapper<NoteLabelEntity> {
    
    /**
     * 根据笔记ID查询关联的标签列表
     */
    List<LabelEntity> selectLabelsByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据笔记ID删除所有标签关联
     */
    int deleteByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据标签ID删除所有笔记关联
     */
    int deleteByLabelId(@Param("labelId") Long labelId);
    
    /**
     * 查询标签被使用的次数
     */
    int countUsageByLabelId(@Param("labelId") Long labelId);
    
    /**
     * 批量插入笔记标签关联
     */
    int batchInsert(@Param("noteId") Long noteId, @Param("labelIds") List<Long> labelIds);
}