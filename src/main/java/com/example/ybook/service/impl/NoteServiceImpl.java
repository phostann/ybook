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
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.mapper.NoteLabelMapper;
import com.example.ybook.mapper.UserNoteInteractionMapper;
import com.example.ybook.service.NoteService;
import com.example.ybook.service.UserNoteInteractionService;
import com.example.ybook.vo.InteractionStatusVO;
import com.example.ybook.vo.NoteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 笔记服务实现
 */
@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, NoteEntity> implements NoteService {

    private final NoteConverter noteConverter;
    private final NoteLabelMapper noteLabelMapper;
    private final LabelMapper labelMapper;
    private final UserNoteInteractionService userNoteInteractionService;
    private final UserNoteInteractionMapper userNoteInteractionMapper;
    private final com.example.ybook.service.CommentService commentService;

    public NoteServiceImpl(NoteConverter noteConverter,
            NoteLabelMapper noteLabelMapper,
            LabelMapper labelMapper,
            UserNoteInteractionService userNoteInteractionService,
            UserNoteInteractionMapper userNoteInteractionMapper,
            com.example.ybook.service.CommentService commentService) {
        this.noteConverter = noteConverter;
        this.noteLabelMapper = noteLabelMapper;
        this.labelMapper = labelMapper;
        this.userNoteInteractionService = userNoteInteractionService;
        this.userNoteInteractionMapper = userNoteInteractionMapper;
        this.commentService = commentService;
    }

    @Override
    public NoteVO getNoteById(Long id) {
        Long userId = CurrentUserContext.requireUserId();
        NoteEntity entity = this.getById(id);
        if (entity == null || !Objects.equals(entity.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 查询关联的标签
        List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(id);
        NoteVO noteVO = noteConverter.entityToVO(entity, labels);
        
        // 设置交互状态
        setInteractionStatus(noteVO, userId);
        
        return noteVO;
    }

    @Override
    public List<NoteVO> listNotesByUserId() {
        Long userId = CurrentUserContext.requireUserId();
        List<NoteEntity> notes = this.baseMapper.selectByUserId(userId);
        return convertNotesToVOsWithLabels(notes);
    }

    @Override
    public PageResult<NoteVO> pageNotesByUserId(Page<NoteEntity> page) {
        Long userId = CurrentUserContext.requireUserId();
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
    public List<NoteVO> listNotesByLabelId(Long labelId) {
        Long userId = CurrentUserContext.requireUserId();
        // 验证标签存在
        LabelEntity label = labelMapper.selectById(labelId);
        if (label == null) {
            throw new BizException(ApiCode.LABEL_NOT_FOUND);
        }

        List<NoteEntity> notes = this.baseMapper.selectByLabelId(labelId, userId);
        return convertNotesToVOsWithLabels(notes);
    }

    @Override
    public PageResult<NoteVO> pageNotesByLabelId(Page<NoteEntity> page, Long labelId) {
        Long userId = CurrentUserContext.requireUserId();
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
    public PageResult<NoteVO> searchNotes(Page<NoteEntity> page, String keyword) {
        Long userId = CurrentUserContext.requireUserId();
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
    public NoteVO createNote(NoteCreateDTO dto) {
        Long userId = CurrentUserContext.getUserId();
        NoteEntity entity = noteConverter.createDTOToEntity(userId, dto);
        entity.setUid(userId);

        this.save(entity);

        // 处理标签关联
        if (dto.getLabelIds() != null && !dto.getLabelIds().isEmpty()) {
            // 验证标签是否存在
            List<LabelEntity> existingLabels = labelMapper.selectByIds(dto.getLabelIds());
            List<Long> validLabelIds = existingLabels.stream()
                    .map(LabelEntity::getId)
                    .collect(Collectors.toList());
            
            if (!validLabelIds.isEmpty()) {
                noteLabelMapper.batchInsert(entity.getId(), validLabelIds);
            }
        }

        return getNoteById(entity.getId());
    }

    @Override
    @Transactional
    public NoteVO updateNote(Long id, NoteUpdateDTO dto) {
        Long userId = CurrentUserContext.requireUserId();
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 更新笔记基本信息
        NoteEntity toUpdate = noteConverter.updateDTOToEntity(id, dto);
        this.updateById(toUpdate);

        // 处理标签关联的更新
        if (dto.getLabelIds() != null) {
            // 删除原有的标签关联
            noteLabelMapper.deleteByNoteId(id);
            
            // 添加新的标签关联
            if (!dto.getLabelIds().isEmpty()) {
                // 验证标签是否存在
                List<LabelEntity> existingLabels = labelMapper.selectByIds(dto.getLabelIds());
                List<Long> validLabelIds = existingLabels.stream()
                        .map(LabelEntity::getId)
                        .collect(Collectors.toList());
                
                if (!validLabelIds.isEmpty()) {
                    noteLabelMapper.batchInsert(id, validLabelIds);
                }
            }
        }

        return getNoteById(id);
    }

    @Override
    @Transactional
    public boolean deleteNote(Long id) {
        Long userId = CurrentUserContext.requireUserId();
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 删除笔记相关的评论（软删除）
        commentService.deleteCommentsByNoteId(id);
        log.info("Deleted comments for noteId: {}", id);

        // 删除标签关联
        noteLabelMapper.deleteByNoteId(id);

        // 删除用户交互记录
        int deletedInteractions = userNoteInteractionMapper.deleteByNoteId(id);
        if (deletedInteractions > 0) {
            log.info("Deleted {} user interactions for noteId: {}", deletedInteractions, id);
        }

        // 删除笔记
        return this.removeById(id);
    }

    @Override
    @Transactional
    public NoteVO togglePin(Long id) {
        Long userId = CurrentUserContext.requireUserId();
        // 验证笔记存在且属于当前用户
        NoteEntity existing = this.getById(id);
        if (existing == null || !Objects.equals(existing.getUid(), userId)) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 切换置顶状态
        existing.setIsTop(existing.getIsTop().equals("1") ? "0" : "1");
        this.updateById(existing);

        return getNoteById(id);
    }

    /**
     * 将笔记实体列表转换为VO列表，并加载标签信息
     */
    private List<NoteVO> convertNotesToVOsWithLabels(List<NoteEntity> notes) {
        Long userId = CurrentUserContext.getUserId();
        List<NoteVO> noteVOs = notes.stream().map(note -> {
            List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(note.getId());
            return noteConverter.entityToVO(note, labels);
        }).collect(Collectors.toList());
        
        // 批量设置交互状态
        if (userId != null && !noteVOs.isEmpty()) {
            batchSetInteractionStatus(noteVOs, userId);
        }
        
        return noteVOs;
    }
    
    /**
     * 设置单个笔记的交互状态
     */
    private void setInteractionStatus(NoteVO noteVO, Long userId) {
        if (userId != null) {
            InteractionStatusVO.InteractionDetailVO status = 
                userNoteInteractionService.getInteractionStatus(noteVO.getId(), userId);
            noteVO.setIsLiked(status.getIsLiked());
            noteVO.setIsFavorited(status.getIsFavorited());
        } else {
            noteVO.setIsLiked(false);
            noteVO.setIsFavorited(false);
        }
    }
    
    /**
     * 批量设置笔记的交互状态
     */
    private void batchSetInteractionStatus(List<NoteVO> noteVOs, Long userId) {
        List<Long> noteIds = noteVOs.stream()
                .map(NoteVO::getId)
                .collect(Collectors.toList());
                
        InteractionStatusVO statusMap = userNoteInteractionService.batchGetInteractionStatus(noteIds, userId);
        
        for (NoteVO noteVO : noteVOs) {
            InteractionStatusVO.InteractionDetailVO status = 
                statusMap.getInteractions().get(noteVO.getId());
            if (status != null) {
                noteVO.setIsLiked(status.getIsLiked());
                noteVO.setIsFavorited(status.getIsFavorited());
            } else {
                noteVO.setIsLiked(false);
                noteVO.setIsFavorited(false);
            }
        }
    }

}