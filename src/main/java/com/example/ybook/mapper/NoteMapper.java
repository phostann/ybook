package com.example.ybook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ybook.entity.NoteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 笔记 Mapper
 */
@Mapper
public interface NoteMapper extends BaseMapper<NoteEntity> {
    
    /**
     * 根据用户ID查询笔记列表
     */
    @Select("SELECT * FROM y_note WHERE uid = #{userId} ORDER BY is_top DESC, update_time DESC")
    List<NoteEntity> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID分页查询笔记
     */
    @Select("SELECT * FROM y_note WHERE uid = #{userId} ORDER BY is_top DESC, update_time DESC")
    Page<NoteEntity> selectPageByUserId(Page<NoteEntity> page, @Param("userId") Long userId);
    
    /**
     * 根据标签ID查询笔记列表
     */
    @Select("SELECT n.* FROM y_note n " +
            "INNER JOIN y_note_label nl ON n.id = nl.note_id " +
            "WHERE nl.label_id = #{labelId} AND n.uid = #{userId} " +
            "ORDER BY n.is_top DESC, n.update_time DESC")
    List<NoteEntity> selectByLabelId(@Param("labelId") Long labelId, @Param("userId") Long userId);
    
    /**
     * 根据标签ID分页查询笔记
     */
    @Select("SELECT n.* FROM y_note n " +
            "INNER JOIN y_note_label nl ON n.id = nl.note_id " +
            "WHERE nl.label_id = #{labelId} AND n.uid = #{userId} " +
            "ORDER BY n.is_top DESC, n.update_time DESC")
    Page<NoteEntity> selectPageByLabelId(Page<NoteEntity> page, @Param("labelId") Long labelId, @Param("userId") Long userId);
    
    /**
     * 根据关键词搜索笔记（标题和内容）
     */
    @Select("SELECT * FROM y_note " +
            "WHERE uid = #{userId} " +
            "AND (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY is_top DESC, update_time DESC")
    Page<NoteEntity> searchNotes(Page<NoteEntity> page, @Param("keyword") String keyword, @Param("userId") Long userId);
}