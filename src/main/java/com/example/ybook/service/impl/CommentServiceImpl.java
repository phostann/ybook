package com.example.ybook.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ybook.common.ApiCode;
import com.example.ybook.common.PageResult;
import com.example.ybook.converter.CommentConverter;
import com.example.ybook.dto.CommentCreateDTO;
import com.example.ybook.dto.CommentUpdateDTO;
import com.example.ybook.entity.CommentEntity;
import com.example.ybook.entity.UserEntity;
import com.example.ybook.exception.BizException;
import com.example.ybook.mapper.CommentMapper;
import com.example.ybook.mapper.NoteMapper;
import com.example.ybook.mapper.UserMapper;
import com.example.ybook.security.CurrentUserContext;
import com.example.ybook.service.CommentService;
import com.example.ybook.vo.CommentListVO;
import com.example.ybook.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 评论服务实现（支持多层嵌套回复和分页）
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    private final CommentConverter commentConverter;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(CommentConverter commentConverter, 
                             NoteMapper noteMapper,
                             UserMapper userMapper) {
        this.commentConverter = commentConverter;
        this.noteMapper = noteMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public CommentVO createComment(CommentCreateDTO dto) {
        Long currentUserId = CurrentUserContext.requireUserId();
        
        // 验证笔记是否存在
        if (noteMapper.selectById(dto.getNoteId()) == null) {
            throw new BizException(ApiCode.NOTE_NOT_FOUND);
        }
        
        CommentEntity comment = commentConverter.toEntity(dto);
        comment.setUserId(currentUserId);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setIsDeleted(0);
        
        // 判断是否为回复评论
        if (dto.getRootCommentId() != null && dto.getReplyToCommentId() != null) {
            // 验证根评论和被回复评论是否存在且属于同一笔记
            CommentEntity rootComment = baseMapper.selectById(dto.getRootCommentId());
            CommentEntity replyToComment = baseMapper.selectById(dto.getReplyToCommentId());
            
            if (rootComment == null || rootComment.getIsDeleted() == 1) {
                throw new BizException(ApiCode.ROOT_COMMENT_NOT_FOUND);
            }
            if (replyToComment == null || replyToComment.getIsDeleted() == 1) {
                throw new BizException(ApiCode.REPLY_COMMENT_NOT_FOUND);
            }
            if (!Objects.equals(rootComment.getNoteId(), dto.getNoteId()) ||
                !Objects.equals(replyToComment.getNoteId(), dto.getNoteId())) {
                throw new BizException(ApiCode.COMMENT_NOTE_MISMATCH);
            }
            
            // 计算评论层级（被回复评论的层级 + 1）
            comment.setCommentLevel(replyToComment.getCommentLevel() + 1);
        } else {
            // 顶级评论
            comment.setRootCommentId(null);
            comment.setReplyToCommentId(null);
            comment.setCommentLevel(0);
        }
        
        // 保存评论
        baseMapper.insert(comment);
        
        // 异步更新统计数据
        asyncUpdateCommentStats(dto.getNoteId(), dto.getRootCommentId());
        
        return getCommentById(comment.getId());
    }

    @Override
    @Transactional
    public CommentVO updateComment(Long commentId, CommentUpdateDTO dto) {
        Long currentUserId = CurrentUserContext.requireUserId();
        
        CommentEntity comment = baseMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BizException(ApiCode.COMMENT_NOT_FOUND);
        }
        
        // 验证权限：只能编辑自己的评论
        if (!Objects.equals(comment.getUserId(), currentUserId)) {
            throw new BizException(ApiCode.COMMENT_ACCESS_DENIED);
        }
        
        comment.setContent(dto.getContent());
        baseMapper.updateById(comment);
        
        return getCommentById(commentId);
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId) {
        Long currentUserId = CurrentUserContext.requireUserId();
        
        CommentEntity comment = baseMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BizException(ApiCode.COMMENT_NOT_FOUND);
        }
        
        // 验证权限：只能删除自己的评论
        if (!Objects.equals(comment.getUserId(), currentUserId)) {
            throw new BizException(ApiCode.COMMENT_DELETE_DENIED);
        }
        
        // 软删除评论
        comment.setIsDeleted(1);
        baseMapper.updateById(comment);
        
        // 异步更新统计数据
        asyncUpdateCommentStats(comment.getNoteId(), comment.getRootCommentId());
        
        return true;
    }

    @Override
    public CommentVO getCommentById(Long commentId) {
        CommentEntity comment = baseMapper.selectById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BizException(ApiCode.COMMENT_NOT_FOUND);
        }
        
        return convertToVOWithDetails(comment);
    }

    @Override
    public PageResult<CommentVO> getRootCommentsByNoteId(Long noteId, int current, int size) {
        Page<CommentEntity> page = new Page<>(current, size);
        Page<CommentEntity> commentPage = baseMapper.selectRootCommentsByNoteId(page, noteId);
        
        List<CommentVO> commentVOs = commentPage.getRecords().stream()
                .map(this::convertToVOWithDetails)
                .collect(Collectors.toList());
        
        return PageResult.<CommentVO>builder()
                .current(commentPage.getCurrent())
                .size(commentPage.getSize())
                .total(commentPage.getTotal())
                .pages(commentPage.getPages())
                .records(commentVOs)
                .build();
    }

    @Override
    public PageResult<CommentVO> getRepliesByRootCommentId(Long rootCommentId, int current, int size) {
        // 验证根评论是否存在
        CommentEntity rootComment = baseMapper.selectById(rootCommentId);
        if (rootComment == null || rootComment.getIsDeleted() == 1) {
            throw new BizException(ApiCode.ROOT_COMMENT_NOT_FOUND);
        }
        
        Page<CommentEntity> page = new Page<>(current, size);
        Page<CommentEntity> replyPage = baseMapper.selectRepliesByRootCommentId(page, rootCommentId);
        
        List<CommentVO> replyVOs = replyPage.getRecords().stream()
                .map(this::convertToVOWithDetails)
                .collect(Collectors.toList());

        return PageResult.<CommentVO>builder()
                .current(replyPage.getCurrent())
                .size(replyPage.getSize())
                .total(replyPage.getTotal())
                .pages(replyPage.getPages())
                .records(replyVOs)
                .build();
    }

    @Override
    public CommentListVO getCommentStatsByNoteId(Long noteId) {
        CommentListVO result = new CommentListVO();
        
        // 统计根评论数量
        Integer rootCount = baseMapper.countRootCommentsByNoteId(noteId);
        result.setTopLevelCount(rootCount.longValue());
        
        // 统计总评论数量
        Integer totalCount = baseMapper.countAllCommentsByNoteId(noteId);
        result.setTotalCount(totalCount.longValue());
        
        return result;
    }

    @Override
    public PageResult<CommentVO> getCommentsByUserId(Long userId, int current, int size) {
        Page<CommentEntity> page = new Page<>(current, size);
        Page<CommentEntity> commentPage = baseMapper.selectCommentsByUserId(page, userId);
        
        List<CommentVO> commentVOs = commentPage.getRecords().stream()
                .map(this::convertToVOWithDetails)
                .collect(Collectors.toList());
        
        return PageResult.<CommentVO>builder()
                .current(commentPage.getCurrent())
                .size(commentPage.getSize())
                .total(commentPage.getTotal())
                .pages(commentPage.getPages())
                .records(commentVOs)
                .build();
    }

    @Override
    public boolean toggleCommentLike(Long commentId) {
        // TODO: 实现评论点赞功能
        return false;
    }

    @Override
    @Transactional
    public void deleteCommentsByNoteId(Long noteId) {
        baseMapper.softDeleteByNoteId(noteId);
        log.info("软删除笔记{}的所有评论", noteId);
    }

    /**
     * 转换为VO并填充详细信息（用户信息、回复上下文等）
     */
    private CommentVO convertToVOWithDetails(CommentEntity comment) {
        CommentVO commentVO = commentConverter.toVO(comment);
        
        // 填充评论用户信息
        UserEntity user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            CommentVO.UserInfo userInfo = new CommentVO.UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
            commentVO.setUser(userInfo);
        }
        
        // 如果是回复评论，填充被回复的评论信息
        if (comment.getReplyToCommentId() != null) {
            CommentEntity replyToComment = baseMapper.selectReplyToComment(comment.getReplyToCommentId());
            if (replyToComment != null) {
                // 填充被回复用户信息
                UserEntity replyToUser = userMapper.selectById(replyToComment.getUserId());
                if (replyToUser != null) {
                    CommentVO.UserInfo replyToUserInfo = new CommentVO.UserInfo();
                    replyToUserInfo.setId(replyToUser.getId());
                    replyToUserInfo.setUsername(replyToUser.getUsername());
                    replyToUserInfo.setNickname(replyToUser.getNickname());
                    replyToUserInfo.setAvatar(replyToUser.getAvatar());
                    commentVO.setReplyToUser(replyToUserInfo);
                }
                
                // 填充被回复的评论内容（截取前50个字符作为上下文）
                String replyToContent = replyToComment.getContent();
                if (replyToContent.length() > 50) {
                    replyToContent = replyToContent.substring(0, 50) + "...";
                }
                commentVO.setReplyToContent(replyToContent);
            }
        }
        
        // TODO: 设置当前用户是否点赞此评论
        commentVO.setIsLiked(false);
        
        return commentVO;
    }

    /**
     * 异步更新评论统计数据
     */
    @Async
    protected void asyncUpdateCommentStats(Long noteId, Long rootCommentId) {
        try {
            // 更新笔记的评论数量
            Integer noteCommentCount = baseMapper.countAllCommentsByNoteId(noteId);
            noteMapper.updateById(new com.example.ybook.entity.NoteEntity() {{
                setId(noteId);
                setCommentCount(noteCommentCount);
            }});
            
            // 如果是回复评论，更新根评论的回复数量
            if (rootCommentId != null) {
                Integer replyCount = baseMapper.countRepliesByRootCommentId(rootCommentId);
                baseMapper.updateRootCommentReplyCount(rootCommentId, replyCount);
            }
        } catch (Exception e) {
            log.error("更新评论统计数据失败: noteId={}, rootCommentId={}", noteId, rootCommentId, e);
        }
    }
}