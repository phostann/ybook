package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.PageResult;
import com.example.ybook.converter.NoteConverter;
import com.example.ybook.dto.NoteCreateDTO;
import com.example.ybook.dto.NoteUpdateDTO;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.entity.NoteEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.LabelMapper;
import com.example.ybook.mapper.NoteMapper;
import com.example.ybook.mapper.NoteLabelMapper;
import com.example.ybook.service.NoteService;
import com.example.ybook.vo.NoteVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 笔记服务实现
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, NoteEntity> implements NoteService {
    
    private final NoteConverter noteConverter;
    private final NoteLabelMapper noteLabelMapper;
    private final LabelMapper labelMapper;
    
    public NoteServiceImpl(NoteConverter noteConverter, 
                          NoteLabelMapper noteLabelMapper, 
                          LabelMapper labelMapper) {
        this.noteConverter = noteConverter;
        this.noteLabelMapper = noteLabelMapper;
        this.labelMapper = labelMapper;
    }
    
    @Override
    public NoteVO getNoteById(Long id, Long userId) {
        NoteEntity entity = this.getById(id);
        if (entity == null || !Objects.equals(entity.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }
        
        // 查询关联的标签
        List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(id);
        return noteConverter.entityToVO(entity, labels);
    }
    
    @Override
    public List<NoteVO> listNotesByUserId(Long userId) {
        List<NoteEntity> notes = this.baseMapper.selectByUserId(userId);
        return convertNotesToVOsWithLabels(notes);
    }
    
    @Override
    public PageResult<NoteVO> pageNotesByUserId(Page<NoteEntity> page, Long userId) {
        Page<NoteEntity> entityPage = this.baseMapper.selectPageByUserId(page, userId);
        List<NoteVO> voList = convertNotesToVOsWithLabels(entityPage.getRecords());
        
        return PageResult.<NoteVO>builder()
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .records(voList)
                .build();
    }
    
    @Override
    public List<NoteVO> listNotesByLabelId(Long labelId, Long userId) {
        // 验证标签存在
        LabelEntity label = labelMapper.selectById(labelId);
        if (label == null) {
            throw new BizException(ApiCode.LABEL_NOT_FOUND);
        }
        
        List<NoteEntity> notes = this.baseMapper.selectByLabelId(labelId, userId);
        return convertNotesToVOsWithLabels(notes);
    }
    
    @Override
    public PageResult<NoteVO> pageNotesByLabelId(Page<NoteEntity> page, Long labelId, Long userId) {
        // 验证标签存在
        LabelEntity label = labelMapper.selectById(labelId);
        if (label == null) {
            throw new BizException(ApiCode.LABEL_NOT_FOUND);
        }
        
        Page<NoteEntity> entityPage = this.baseMapper.selectPageByLabelId(page, labelId, userId);
        List<NoteVO> voList = convertNotesToVOsWithLabels(entityPage.getRecords());
        
        return PageResult.<NoteVO>builder()
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .records(voList)
                .build();
    }
    
    @Override
    public PageResult<NoteVO> searchNotes(Page<NoteEntity> page, String keyword, Long userId) {
        Page<NoteEntity> entityPage = this.baseMapper.searchNotes(page, keyword, userId);
        List<NoteVO> voList = convertNotesToVOsWithLabels(entityPage.getRecords());
        
        return PageResult.<NoteVO>builder()
                .current(entityPage.getCurrent())
                .size(entityPage.getSize())
                .total(entityPage.getTotal())
                .pages(entityPage.getPages())
                .records(voList)
                .build();
    }
    
    @Override
    @Transactional
    public NoteVO createNote(NoteCreateDTO dto, Long userId) {
        // 转换并保存笔记
        NoteEntity entity = noteConverter.createDTOToEntity(dto);
        entity.setUid(userId);
        
        this.save(entity);
        
        return getNoteById(entity.getId(), userId);
    }
    
    @Override
    @Transactional
    public NoteVO updateNote(Long id, NoteUpdateDTO dto, Long userId) {
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }
        
        // 更新笔记基本信息
        NoteEntity toUpdate = noteConverter.updateDTOToEntity(id, dto);
        
        this.updateById(toUpdate);
        
        return getNoteById(id, userId);
    }
    
    @Override
    @Transactional
    public boolean deleteNote(Long id, Long userId) {
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }
        
        // 删除标签关联
        noteLabelMapper.deleteByNoteId(id);
        
        // 删除笔记
        return this.removeById(id);
    }
    
    @Override
    @Transactional
    public NoteVO togglePin(Long id, Long userId) {
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }
        
        // 切换置顶状态
        existing.setIsTop(existing.getIsTop().equals("1") ? "0" : "1");
        this.updateById(existing);
        
        return getNoteById(id, userId);
    }
    
    /**
     * 将笔记实体列表转换为VO列表，并加载标签信息
     */
    private List<NoteVO> convertNotesToVOsWithLabels(List<NoteEntity> notes) {
        return notes.stream().map(note -> {
            List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(note.getId());
            return noteConverter.entityToVO(note, labels);
        }).collect(Collectors.toList());
    }
    
}