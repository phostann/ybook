package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.InteractionType;
import com.example.ybook.converter.NoteConverter;
import com.example.ybook.entity.LabelEntity;
import com.example.ybook.entity.NoteEntity;
import com.example.ybook.entity.UserNoteInteractionEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.LabelMapper;
import com.example.ybook.mapper.NoteMapper;
import com.example.ybook.mapper.NoteLabelMapper;
import com.example.ybook.mapper.UserNoteInteractionMapper;
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.service.UserNoteInteractionService;
import com.example.ybook.vo.InteractionStatusVO;
import com.example.ybook.vo.NoteVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户笔记交互服务实现
 */
@Slf4j
@Service
public class UserNoteInteractionServiceImpl extends ServiceImpl<UserNoteInteractionMapper, UserNoteInteractionEntity>
        implements UserNoteInteractionService {

    private final NoteMapper noteMapper;
    private final NoteConverter noteConverter;
    private final NoteLabelMapper noteLabelMapper;
    private final LabelMapper labelMapper;

    public UserNoteInteractionServiceImpl(NoteMapper noteMapper, 
                                        NoteConverter noteConverter,
                                        NoteLabelMapper noteLabelMapper,
                                        LabelMapper labelMapper) {
        this.noteMapper = noteMapper;
        this.noteConverter = noteConverter;
        this.noteLabelMapper = noteLabelMapper;
        this.labelMapper = labelMapper;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long noteId) {
        Long userId = CurrentUserContext.requireUserId();
        
        // 验证笔记是否存在
        NoteEntity note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 获取或创建交互记录
        UserNoteInteractionEntity interaction = getOrCreateInteraction(userId, noteId);
        
        // 切换点赞状态
        boolean isLiked = interaction.isLiked();
        interaction.setLiked(!isLiked);
        
        // 如果所有交互都取消了，删除记录；否则更新
        if (!interaction.hasAnyInteraction()) {
            this.removeById(interaction.getId());
        } else {
            this.updateById(interaction);
        }
        
        // 异步更新计数
        updateLikeCountAsync(noteId);
        
        return !isLiked; // 返回新的点赞状态
    }

    @Override
    @Transactional
    public boolean toggleFavorite(Long noteId) {
        Long userId = CurrentUserContext.requireUserId();
        
        // 验证笔记是否存在
        NoteEntity note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }

        // 获取或创建交互记录
        UserNoteInteractionEntity interaction = getOrCreateInteraction(userId, noteId);
        
        // 切换收藏状态
        boolean isFavorited = interaction.isFavorited();
        interaction.setFavorited(!isFavorited);
        
        // 如果所有交互都取消了，删除记录；否则更新
        if (!interaction.hasAnyInteraction()) {
            this.removeById(interaction.getId());
        } else {
            this.updateById(interaction);
        }
        
        // 异步更新计数
        updateFavoriteCountAsync(noteId);
        
        return !isFavorited; // 返回新的收藏状态
    }

    @Override
    public InteractionStatusVO.InteractionDetailVO getInteractionStatus(Long noteId, Long userId) {
        if (userId == null) {
            userId = CurrentUserContext.requireUserId();
        }
        
        UserNoteInteractionEntity interaction = baseMapper.selectByUserIdAndNoteId(userId, noteId);
        
        InteractionStatusVO.InteractionDetailVO detail = new InteractionStatusVO.InteractionDetailVO();
        if (interaction != null) {
            detail.setIsLiked(interaction.isLiked());
            detail.setIsFavorited(interaction.isFavorited());
            detail.setIsShared(interaction.isShared());
        } else {
            detail.setIsLiked(false);
            detail.setIsFavorited(false);
            detail.setIsShared(false);
        }
        
        return detail;
    }

    @Override
    public InteractionStatusVO batchGetInteractionStatus(List<Long> noteIds, Long userId) {
        if (userId == null) {
            userId = CurrentUserContext.requireUserId();
        }
        
        List<UserNoteInteractionEntity> interactions = baseMapper.selectBatchByUserIdAndNoteIds(userId, noteIds);
        
        Map<Long, InteractionStatusVO.InteractionDetailVO> resultMap = new HashMap<>();
        
        // 初始化所有笔记为未交互状态
        for (Long noteId : noteIds) {
            InteractionStatusVO.InteractionDetailVO detail = new InteractionStatusVO.InteractionDetailVO();
            detail.setIsLiked(false);
            detail.setIsFavorited(false);
            detail.setIsShared(false);
            resultMap.put(noteId, detail);
        }
        
        // 设置实际的交互状态
        for (UserNoteInteractionEntity interaction : interactions) {
            InteractionStatusVO.InteractionDetailVO detail = resultMap.get(interaction.getNoteId());
            if (detail != null) {
                detail.setIsLiked(interaction.isLiked());
                detail.setIsFavorited(interaction.isFavorited());
                detail.setIsShared(interaction.isShared());
            }
        }
        
        InteractionStatusVO result = new InteractionStatusVO();
        result.setInteractions(resultMap);
        return result;
    }

    @Override
    public Page<NoteVO> getUserFavoriteNotes(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            userId = CurrentUserContext.requireUserId();
        }
        
        // 创建分页对象
        Page<Long> noteIdPage = new Page<>(pageNum, pageSize);
        
        // 分页查询收藏的笔记ID
        Page<Long> favoriteNoteIdPage = baseMapper.selectFavoriteNoteIdsByUserId(noteIdPage, userId);
        
        List<Long> favoriteNoteIds = favoriteNoteIdPage.getRecords();
        if (favoriteNoteIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        
        // 查询笔记详情并转换为VO
        List<NoteVO> noteVOs = new ArrayList<>();
        for (Long noteId : favoriteNoteIds) {
            try {
                // 直接构造 NoteVO，避免循环依赖
                NoteEntity noteEntity = noteMapper.selectById(noteId);
                if (noteEntity != null) {
                    List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(noteId);
                    NoteVO noteVO = noteConverter.entityToVO(noteEntity, labels);
                    // 设置收藏状态为true（因为这是收藏列表）
                    noteVO.setIsLiked(false); // 需要单独查询
                    noteVO.setIsFavorited(true);
                    noteVOs.add(noteVO);
                }
            } catch (Exception e) {
                log.warn("Failed to get note detail for noteId: {}", noteId, e);
            }
        }
        
        // 构造分页结果
        Page<NoteVO> result = new Page<>(pageNum, pageSize);
        result.setRecords(noteVOs);
        result.setTotal(favoriteNoteIdPage.getTotal());
        
        return result;
    }

    @Override
    public Page<NoteVO> getUserLikedNotes(Long userId, int pageNum, int pageSize) {
        if (userId == null) {
            userId = CurrentUserContext.requireUserId();
        }
        
        // 创建分页对象
        Page<Long> noteIdPage = new Page<>(pageNum, pageSize);
        
        // 分页查询点赞的笔记ID
        Page<Long> likedNoteIdPage = baseMapper.selectLikedNoteIdsByUserId(noteIdPage, userId);
        
        List<Long> likedNoteIds = likedNoteIdPage.getRecords();
        if (likedNoteIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        
        // 查询笔记详情并转换为VO
        List<NoteVO> noteVOs = new ArrayList<>();
        for (Long noteId : likedNoteIds) {
            try {
                // 直接构造 NoteVO，避免循环依赖
                NoteEntity noteEntity = noteMapper.selectById(noteId);
                if (noteEntity != null) {
                    List<LabelEntity> labels = noteLabelMapper.selectLabelsByNoteId(noteId);
                    NoteVO noteVO = noteConverter.entityToVO(noteEntity, labels);
                    // 设置点赞状态为true（因为这是点赞列表）
                    noteVO.setIsLiked(true);
                    noteVO.setIsFavorited(false); // 需要单独查询
                    noteVOs.add(noteVO);
                }
            } catch (Exception e) {
                log.warn("Failed to get note detail for noteId: {}", noteId, e);
            }
        }
        
        // 构造分页结果
        Page<NoteVO> result = new Page<>(pageNum, pageSize);
        result.setRecords(noteVOs);
        result.setTotal(likedNoteIdPage.getTotal());
        
        return result;
    }

    @Override
    @Async
    public void updateLikeCountAsync(Long noteId) {
        try {
            Long likeCount = baseMapper.countLikesByNoteId(noteId);
            
            NoteEntity note = new NoteEntity();
            note.setId(noteId);
            note.setLikeCount(likeCount.intValue());
            
            noteMapper.updateById(note);
            log.debug("Updated like count for noteId: {}, count: {}", noteId, likeCount);
        } catch (Exception e) {
            log.error("Failed to update like count for noteId: {}", noteId, e);
        }
    }

    @Override
    @Async
    public void updateFavoriteCountAsync(Long noteId) {
        try {
            Long favoriteCount = baseMapper.countFavoritesByNoteId(noteId);
            
            NoteEntity note = new NoteEntity();
            note.setId(noteId);
            note.setCollectCount(favoriteCount.intValue());
            
            noteMapper.updateById(note);
            log.debug("Updated favorite count for noteId: {}, count: {}", noteId, favoriteCount);
        } catch (Exception e) {
            log.error("Failed to update favorite count for noteId: {}", noteId, e);
        }
    }

    /**
     * 获取或创建交互记录
     */
    private UserNoteInteractionEntity getOrCreateInteraction(Long userId, Long noteId) {
        UserNoteInteractionEntity interaction = baseMapper.selectByUserIdAndNoteId(userId, noteId);
        
        if (interaction == null) {
            interaction = new UserNoteInteractionEntity();
            interaction.setUserId(userId);
            interaction.setNoteId(noteId);
            interaction.setInteractionType(0);
            this.save(interaction);
        }
        
        return interaction;
    }
}