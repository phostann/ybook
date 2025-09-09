package com.example.ybook.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ybook.common.PageResult;
import com.example.ybook.dto.NoteCreateDTO;
import com.example.ybook.dto.NoteUpdateDTO;
import com.example.ybook.entity.NoteEntity;
import com.example.ybook.vo.NoteVO;

import java.util.List;

/**
 * 笔记服务接口
 */
public interface NoteService extends IService<NoteEntity> {
    
    /**
     * 根据笔记ID获取笔记详情（包含标签信息）
     */
    NoteVO getNoteById(Long id);
    
    /**
     * 获取用户的所有笔记列表
     */
    List<NoteVO> listNotesByUserId();
    
    /**
     * 分页获取用户的笔记列表
     */
    PageResult<NoteVO> pageNotesByUserId(Page<NoteEntity> page);
    
    /**
     * 根据标签ID获取笔记列表
     */
    List<NoteVO> listNotesByLabelId(Long labelId);
    
    /**
     * 根据标签ID分页获取笔记列表
     */
    PageResult<NoteVO> pageNotesByLabelId(Page<NoteEntity> page, Long labelId);
    
    /**
     * 根据关键词搜索笔记
     */
    PageResult<NoteVO> searchNotes(Page<NoteEntity> page, String keyword);
    
    /**
     * 创建笔记
     */
    NoteVO createNote(NoteCreateDTO dto);
    
    /**
     * 更新笔记
     */
    NoteVO updateNote(Long id, NoteUpdateDTO dto);
    
    /**
     * 删除笔记
     */
    boolean deleteNote(Long id);
    
    /**
     * 切换笔记置顶状态
     */
    NoteVO togglePin(Long id);
}