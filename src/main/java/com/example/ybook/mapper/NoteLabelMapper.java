package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.entity.NoteLabelEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 笔记标签关联 Mapper
 */
@Mapper
public interface NoteLabelMapper extends BaseMapper<NoteLabelEntity> {
    
    /**
     * 根据笔记ID查询关联的标签列表
     */
    @Select("SELECT l.* FROM y_label l " +
            "INNER JOIN y_note_label nl ON l.id = nl.label_id " +
            "WHERE nl.note_id = #{noteId} " +
            "ORDER BY l.name")
    List<LabelEntity> selectLabelsByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据笔记ID删除所有标签关联
     */
    @Delete("DELETE FROM y_note_label WHERE note_id = #{noteId}")
    int deleteByNoteId(@Param("noteId") Long noteId);
    
    /**
     * 根据标签ID删除所有笔记关联
     */
    @Delete("DELETE FROM y_note_label WHERE label_id = #{labelId}")
    int deleteByLabelId(@Param("labelId") Long labelId);
    
    /**
     * 查询标签被使用的次数
     */
    @Select("SELECT COUNT(*) FROM y_note_label WHERE label_id = #{labelId}")
    int countUsageByLabelId(@Param("labelId") Long labelId);
    
    /**
     * 批量插入笔记标签关联
     */
    @org.apache.ibatis.annotations.Insert("<script>" +
            "INSERT INTO y_note_label (note_id, label_id) VALUES " +
            "<foreach collection='labelIds' item='labelId' separator=','>" +
            "(#{noteId}, #{labelId})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("noteId") Long noteId, @Param("labelIds") List<Long> labelIds);
}